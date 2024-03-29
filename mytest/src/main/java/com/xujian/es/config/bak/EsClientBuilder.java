package com.xujian.es.config.bak;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * http://www.appblog.cn/2019/06/04/ElasticSearch%207.x%20%E9%9B%86%E6%88%90RestHighLevelClient/
 *
 * Created by xujian on 2019-07-31
 */
@Component
public class EsClientBuilder {
    private int connectTimeoutMillis = 1000;
    private int socketTimeoutMillis = 30000;
    private int connectionRequestTimeoutMillis = 500;
    private int maxConnectPerRoute = 10;
    private int maxConnectTotal = 30;
    private final List<HttpHost> httpHosts;

    private EsClientBuilder(List<HttpHost> httpHosts) {
        this.httpHosts = httpHosts;
    }

    public EsClientBuilder setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    public EsClientBuilder setSocketTimeoutMillis(int socketTimeoutMillis) {
        this.socketTimeoutMillis = socketTimeoutMillis;
        return this;
    }

    public EsClientBuilder setConnectionRequestTimeoutMillis(int connectionRequestTimeoutMillis) {
        this.connectionRequestTimeoutMillis = connectionRequestTimeoutMillis;
        return this;
    }

    public EsClientBuilder setMaxConnectPerRoute(int maxConnectPerRoute) {
        this.maxConnectPerRoute = maxConnectPerRoute;
        return this;
    }

    public EsClientBuilder setMaxConnectTotal(int maxConnectTotal) {
        this.maxConnectTotal = maxConnectTotal;
        return this;
    }

    public static EsClientBuilder build(List<HttpHost> httpHosts) {
        return new EsClientBuilder(httpHosts);
    }

    public RestHighLevelClient create() {
        HttpHost[] httpHostArr = httpHosts.toArray(new HttpHost[0]);
        RestClientBuilder builder = RestClient.builder(httpHostArr);
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeoutMillis);
            requestConfigBuilder.setSocketTimeout(socketTimeoutMillis);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeoutMillis);
            return requestConfigBuilder;
        });
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectTotal);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }
}
