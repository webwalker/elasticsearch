package com.xujian.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.xujian.es.domain.common.EsResponse;
import com.xujian.es.domain.common.Page;
import com.xujian.es.domain.model.BookModel;
import com.xujian.es.domain.vo.BookRequestVO;
import com.xujian.es.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xujian on 2019-07-31
 */
@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private static final String INDEX_NAME = "book";
    private static final String INDEX_TYPE = "_doc";

    @Autowired
    private RestHighLevelClient client;


    @Override
    public Page<BookModel> list(BookRequestVO bookRequestVO) {
        int pageNo = bookRequestVO.getPageNo();
        int pageSize = bookRequestVO.getPageSize();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);
        //sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC));
        //sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
//        sourceBuilder.query(QueryBuilders.matchAllQuery());

        //聚集函数
//        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_name")
//                .field("name");
//        aggregation.subAggregation(AggregationBuilders.avg("average_age"))
//                .field("age");
//        sourceBuilder.aggregation(aggregation);

        //构建Query
//        MatchQueryBuilder queryBuilder = new MatchQueryBuilder("name", bookRequestVO.getName());
//        queryBuilder.fuzziness(Fuzziness.AUTO);
//        queryBuilder.prefixLength(3);
//        queryBuilder.maxExpansions(10);

//        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", bookRequestVO.getName())
//                .fuzziness(Fuzziness.AUTO)
//                .prefixLength(3)
//                .maxExpansions(10);

        //BoolQueryBuilder
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(bookRequestVO.getName())) {
            queryBuilder.must(QueryBuilders.matchQuery("name", bookRequestVO.getName()));
        }
        if (StringUtils.isNotBlank(bookRequestVO.getAuthor())) {
            queryBuilder.must(QueryBuilders.matchQuery("author", bookRequestVO.getAuthor()));
        }
        if (null != bookRequestVO.getStatus()) {
            queryBuilder.must(QueryBuilders.termQuery("status", bookRequestVO.getStatus()));
        }
        if (StringUtils.isNotBlank(bookRequestVO.getSellTime())) {
            queryBuilder.must(QueryBuilders.termQuery("sellTime", bookRequestVO.getSellTime()));
        }
        if (StringUtils.isNotBlank(bookRequestVO.getCategories())) {
            String[] categoryArr = bookRequestVO.getCategories().split(",");
            List<Integer> categoryList = Arrays.asList(categoryArr).stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
            BoolQueryBuilder categoryBoolQueryBuilder = QueryBuilders.boolQuery();
            for (Integer category : categoryList) {
                categoryBoolQueryBuilder.should(QueryBuilders.termQuery("category", category));
            }
            queryBuilder.must(categoryBoolQueryBuilder);
        }
        //filter
        if (!ObjectUtils.isEmpty(bookRequestVO.getGtPrice()) && !ObjectUtils.isEmpty(bookRequestVO.getLtPrice())) {
            queryBuilder.filter(QueryBuilders.rangeQuery("price")
                    .from(bookRequestVO.getGtPrice())
                    .to(bookRequestVO.getLtPrice()));
        }

        //boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        sourceBuilder.query(queryBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX_NAME); //在指定的索引下查找
        searchRequest.source(sourceBuilder);
        //searchRequest.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);

        try {
            //client.searchAsync();
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            RestStatus restStatus = searchResponse.status();
            if (restStatus != RestStatus.OK) {
                return null;
            }

            List<BookModel> list = new ArrayList<>();
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit : searchHits.getHits()) {
                String source = hit.getSourceAsString();
                //Map<String, Object> maps = getResponse.getSourceAsMap();
                BookModel book = JSON.parseObject(source, BookModel.class);
                list.add(book);
            }

            TotalHits totalHits = searchHits.getTotalHits();

            Page<BookModel> page = new Page<>(pageNo, pageSize, totalHits.value, list);

            TimeValue took = searchResponse.getTook();
            log.info("查询成功！请求参数: {}, 用时{}毫秒", searchRequest.source().toString(), took.millis());

            return page;
        } catch (IOException e) {
            log.error("查询失败！原因: {}", e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void save(BookModel bookModel) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("id", bookModel.getId());
        jsonMap.put("name", bookModel.getName());
        jsonMap.put("author", bookModel.getAuthor());
        jsonMap.put("category", bookModel.getCategory());
        jsonMap.put("price", bookModel.getPrice());
        jsonMap.put("sellTime", bookModel.getSellTime());
        jsonMap.put("sellReason", bookModel.getSellReason());
        jsonMap.put("status", bookModel.getStatus());

        IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                .id(String.valueOf(bookModel.getId()))
                .source(jsonMap); //方式1

        //方式2
        //String jsonString = "{\"user\":\"kimchy\",\"postDate\":\"2013-01-30\",\"message\":\"trying out Elasticsearch\"}";
        //indexRequest.source(jsonString, XContentType.JSON);

        //方式3
//        indexRequest.source("id", bookModel.getId())
//                .source("name", bookModel.getName())
//                .source("author", bookModel.getAuthor());

        //同步
//        try {
//            client.index(indexRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //异步创建
        client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                String index = indexResponse.getIndex();
                String id = indexResponse.getId();
                long version = indexResponse.getVersion();

                log.info("Index: {}, Id: {}, Version: {}", index, id, version);

                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    log.info("写入文档"); //文档第一次被创建
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    log.info("修改文档"); //文档被修改
                }
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    log.warn("部分分片写入成功"); //成功的分片数与总分片数不相等,也就是说有失败的
                }
                if (shardInfo.getFailed() > 0) {
                    //失败原因
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason();
                        log.warn("失败原因: {}", reason);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public EsResponse update(BookModel bookModel) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("price", bookModel.getPrice());
        jsonMap.put("sellReason", bookModel.getSellReason());
        UpdateRequest request = new UpdateRequest(INDEX_NAME, String.valueOf(bookModel.getId()));
        request.doc(jsonMap);
        try {
            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
            return EsResponse.ok(updateResponse);
        } catch (IOException e) {
            log.error("更新失败！原因: {}", e.getMessage(), e);
        }
        return EsResponse.fail();
    }

    @Override
    public EsResponse delete(int id) {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, String.valueOf(id));
        try {
            DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
            if (deleteResponse.status() == RestStatus.OK) {
                log.info("删除成功！id: {}", id);
            }
            return EsResponse.ok(deleteResponse);
        } catch (IOException e) {
            log.error("删除失败！原因: {}", e.getMessage(), e);
        }
        return EsResponse.fail();
    }

    @Override
    public BookModel detail(int id) {
        GetRequest getRequest = new GetRequest(INDEX_NAME, String.valueOf(id));
        try {
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                String source = getResponse.getSourceAsString();
                //Map<String, Object> maps = getResponse.getSourceAsMap();
                BookModel book = JSON.parseObject(source, BookModel.class);
                return book;
            }
        } catch (IOException e) {
            log.error("查看失败！原因: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean exist(int id) {
        GetRequest getRequest = new GetRequest(INDEX_NAME, String.valueOf(id));
        try {
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("check exist 失败！原因: {}", e.getMessage(), e);
        }
        return false;
    }

    //批量处理多个请求
    public Boolean batch() { //Collection<MessageContent> c
        //批量操作请求：
//        BulkRequest bulkRequest = new BulkRequest();
//        for (MessageContent m : c) {
//            try {
//                //添加请求，这里不一定是插入，插入+修改+删除组合都可以
//                bulkRequest.add(new IndexRequest(index).id(m.getMessageId()).source(Object2JsonUtil.ObjectToMap(m)));
//                BulkResponse r = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//                if (r.hasFailures()) {
//                    return false;
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return true;
    }
}