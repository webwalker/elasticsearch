package com.xujian.es.service;

import com.alibaba.fastjson.JSON;
import com.xujian.es.domain.common.Page;
import com.xujian.es.domain.model.IndexResourceInfoModel;
import com.xujian.es.domain.vo.IndexRequestVO;
import com.xujian.es.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by xujian on 2019-08-01
 */
@Service
@Slf4j
public class SearchService {
    private final static String INDEX = "ss_ware_resource_info";

    @Autowired
    private RestHighLevelClient client;

    public Page<IndexResourceInfoModel> search(IndexRequestVO requestVO) {
        long begin = System.currentTimeMillis();
        int pageNo = requestVO.getPageNo();
        int pageSize = requestVO.getPageSize();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        int pageNumber = (pageNo - 1) * pageSize;
        sourceBuilder.from(pageNumber); //表示从第几行开始
        sourceBuilder.size(pageSize);
        sourceBuilder.sort(new FieldSortBuilder("year").order(SortOrder.DESC));
        //分组
        List<String> groupList = new ArrayList<String>(Arrays.asList(
                new String[]{
                        "year",
                        "basic_id_contact_simple",
                        "basic_id_creator_simple",
                        "basic_id_keyword_simple"}));
        for (String group : groupList) {
            sourceBuilder.aggregation(AggregationBuilders.terms(group).field(group));
        }
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<h2>");
        highlightBuilder.postTags("</h2>");
        List<String> highlightFieldList = Arrays.asList(new String[]{"basic_description_IK", "basic_id_contact_IK"});
        for (String s : highlightFieldList) {
            highlightBuilder.field(s);
        }
        sourceBuilder.highlighter(highlightBuilder);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //termQuery 模糊查询
        queryBuilder.must(QueryBuilders.termQuery("basic_id_contact_IK", "海南省"));
        //queryBuilder.must(QueryBuilders.termQuery("basic_id_creator_simple", "郭秀凤"))
        queryBuilder.must(QueryBuilders.termQuery("basic_description_IK", "中国"));
        queryBuilder.must(QueryBuilders.termQuery("basic_id_keyword_IK", "五指山市"));
        sourceBuilder.query(queryBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = searchResponse.status();
            if (restStatus != RestStatus.OK) {
                return null;
            }

            //分组信息
            for (String group : groupList) {
                TermsCollect(searchResponse, group);
            }

            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();
            TotalHits totalHits = searchHits.getTotalHits();
            log.info("搜索到记录条数：" + totalHits.value);

            //原始内容列表
//            List<IndexResourceInfoModel> indexList = new ArrayList<>();
//            for (SearchHit hit : hits) {
//                String source = hit.getSourceAsString();
//                IndexResourceInfoModel index = JSON.parseObject(source, IndexResourceInfoModel.class);
//                indexList.add(index);
//            }

            TimeValue took = searchResponse.getTook();
            log.info("查询成功！请求参数: {}", searchRequest.source().toString());
            log.info("远程用时{}毫秒", took.millis());

            //包含高亮内容列表
            List<IndexResourceInfoModel> hignlightList = new ArrayList<IndexResourceInfoModel>();
            getResultList(highlightFieldList, hits, hignlightList);

            //详情
            log.info("【分页搜索结果详情】");
            for (IndexResourceInfoModel indexResourceInfoModel : hignlightList) {
                log.info(indexResourceInfoModel.toString());
            }
            log.info("搜索耗时：" + (System.currentTimeMillis() - begin) + "毫秒");

            Page<IndexResourceInfoModel> page = new Page<>(pageNo, pageSize, totalHits.value, hignlightList);
            return page;
        } catch (IOException e) {
            log.error("查询失败！原因: {}", e.getMessage(), e);
        }

        return null;
    }

    private static void TermsCollect(SearchResponse response, String keywordTerm) {
        Terms terms = response.getAggregations().get(keywordTerm);
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        log.info("【" + keywordTerm + "分组情况】");
        for (Terms.Bucket bucket : buckets) {
            log.info(bucket.getKey() + " " + bucket.getDocCount());
        }
        log.info("");
    }

    //搜索出来的记录放到IndexResourceInfoModel对象里面去
    private static void getResultList(List<String> highlightFieldList, SearchHit[] hits, List<IndexResourceInfoModel> indexResourceInfoModelList) {
        for (SearchHit hit : hits) {
            IndexResourceInfoModel indexResourceInfoModel = new IndexResourceInfoModel();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (Map.Entry<String, Object> stringObjectEntry : sourceAsMap.entrySet()) {
                String fieldName = stringObjectEntry.getKey();
                if (highlightFieldList.contains(fieldName)) {
                    StringBuilder sb = new StringBuilder();
                    Text[] text = hit.getHighlightFields().get(fieldName).getFragments();
                    for (Text str : text) {
                        sb.append(str.string());
                    }
                    ReflectionUtil.setFieldValue(indexResourceInfoModel, fieldName, sb.toString());
                } else {
                    ReflectionUtil.setFieldValue(indexResourceInfoModel, fieldName, stringObjectEntry.getValue());
                }
            }
            indexResourceInfoModelList.add(indexResourceInfoModel);
        }
    }
}
