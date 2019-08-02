package com.xujian.es.config;

import com.xujian.es.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

/**
 * 初始化es index
 * Created by xujian on 2019-08-01
 */
@Slf4j
@Component
public class EsInitializer {
    private RestHighLevelClient client;

    public void init(RestHighLevelClient client, HttpHost[] hosts) {
        this.client = client;
        try {
            if (this.indexExist(EsConstant.BOOK_INDEX)) {
                log.info("索引{}已存在.", EsConstant.BOOK_INDEX);
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(EsConstant.BOOK_INDEX);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2));
            request.mapping(EsConstant.CREATE_BOOK_INDEX, XContentType.JSON);

            CreateIndexResponse res = client.indices().create(request, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new CustomException("初始化失败");
            }
            log.info("索引{}初始化成功.", EsConstant.BOOK_INDEX);
        } catch (Exception e) {
            log.error("注意初始化es失败", e);
        }
    }

    public boolean indexExist(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }
}
