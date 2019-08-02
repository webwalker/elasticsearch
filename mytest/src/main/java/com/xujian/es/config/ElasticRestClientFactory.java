package com.xujian.es.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created by xujian on 2019-07-31
 */
@Slf4j
public class ElasticRestClientFactory {
    @Autowired
    EsInitializer initializer;

    // 连接目标url最大超时
    public static int CONNECT_TIMEOUT_MILLIS = 3000;
    // 等待响应（读数据）最大超时
    public static int SOCKET_TIMEOUT_MILLIS = 6000;
    // 从连接池中获取可用连接最大超时时间
    public static int CONNECTION_REQUEST_TIMEOUT_MILLIS = 2000;

    // 连接池中的最大连接数
    public static int MAX_CONN_TOTAL = 15;
    // 连接同一个route最大的并发数
    public static int MAX_CONN_PER_ROUTE = 10;
    // 用户名
    public static String USER_NAME = "xujian";
    // 密码
    public static String PASSWORD = "123456";

    private static HttpHost[] HTTP_HOST;
    private RestClientBuilder builder;
    private RestClient restClient;
    private RestHighLevelClient restHighLevelClient;

    private static ElasticRestClientFactory restClientFactory = new ElasticRestClientFactory();

    private ElasticRestClientFactory() {
    }

    public static ElasticRestClientFactory build(HttpHost[] httpHost, Integer maxConnectNum, Integer maxConnectPerRoute) {
        HTTP_HOST = httpHost;
        MAX_CONN_TOTAL = maxConnectNum;
        MAX_CONN_PER_ROUTE = maxConnectPerRoute;
        return restClientFactory;
    }

    public static ElasticRestClientFactory build(HttpHost[] httpHost, Integer connectTimeOut, Integer socketTimeOut,
                                                 Integer connectionRequestTime, Integer maxConnectNum, Integer maxConnectPerRoute,
                                                 String userName, String password) {
        HTTP_HOST = httpHost;
        CONNECT_TIMEOUT_MILLIS = connectTimeOut;
        SOCKET_TIMEOUT_MILLIS = socketTimeOut;
        CONNECTION_REQUEST_TIMEOUT_MILLIS = connectionRequestTime;
        MAX_CONN_TOTAL = maxConnectNum;
        MAX_CONN_PER_ROUTE = maxConnectPerRoute;
        USER_NAME = userName;
        PASSWORD = password;
        return restClientFactory;
    }

    public void init() {
        builder = RestClient.builder(HTTP_HOST);
        setConnectTimeOutConfig();
        setMutiConnectConfig();

        //check it.
//        if (restClient!=null){
//            try {
//                restClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        restClient = builder.build();
        restHighLevelClient = new RestHighLevelClient(builder);
        initializer.init(restHighLevelClient, HTTP_HOST);

        log.info("Elasticsearch highLevelRestClient init successful");
    }

    // 配置连接延时时间
    public void setConnectTimeOutConfig() {
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            requestConfigBuilder.setSocketTimeout(SOCKET_TIMEOUT_MILLIS);
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLIS);
            return requestConfigBuilder;
        });
    }

    // 使用异步httpclient时设置并发连接数
    public void setMutiConnectConfig() {
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(MAX_CONN_TOTAL);
            httpClientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE);

            //认证信息
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USER_NAME, PASSWORD));
            //httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

            return httpClientBuilder;
        });
    }

    public RestClient getClient() {
        return restClient;
    }

    public RestHighLevelClient getRestHighClient() {
        return restHighLevelClient;
    }

    public void close() {
        if (restClient != null) {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Elasticsearch highLevelRestClient closed");
    }
}
