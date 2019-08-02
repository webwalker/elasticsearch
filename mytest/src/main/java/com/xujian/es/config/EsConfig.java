package com.xujian.es.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by xujian on 2019-07-31
 */
@Slf4j
@Configuration
public class EsConfig {
    private static final int ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME = "http";

    /**
     * elasticsearch 连接地址多个地址使用,分隔
     */
    @Value("${elasticsearch.ipAddrs}")
    private String[] ipAddrs;

    /**
     * 连接目标url最大超时
     */
    @Value("${elasticsearch.client.connectTimeOut}")
    private Integer connectTimeOut;

    /**
     * 等待响应（读数据）最大超时
     */
    @Value("${elasticsearch.client.socketTimeOut}")
    private Integer socketTimeOut;

    /**
     * 从连接池中获取可用连接最大超时时间
     */
    @Value("${elasticsearch.client.connectionRequestTime}")
    private Integer connectionRequestTime;

    /**
     * 连接池中的最大连接数
     */
    @Value("${elasticsearch.client.maxConnectNum}")
    private Integer maxConnectNum;

    /**
     * 连接同一个route最大的并发数
     */
    @Value("${elasticsearch.client.maxConnectPerRoute}")
    private Integer maxConnectPerRoute;

    /**
     * 连接用户名
     */
    @Value("${elasticsearch.client.username}")
    private String userName;

    /**
     * 连接密码
     */
    @Value("${elasticsearch.client.password}")
    private String password;

    //-------------------------first-----------------------//
    @Bean
    public HttpHost[] httpHost() {
        HttpHost[] httpHosts = new HttpHost[ipAddrs.length];
        for (int i = 0; i < ipAddrs.length; i++) {
            String[] ipAddr = ipAddrs[i].split(":");
            httpHosts[i] = new HttpHost(ipAddr[0], Integer.valueOf(ipAddr[1]), HTTP_SCHEME);
        }
        return httpHosts;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public ElasticRestClientFactory getFactory() {
        return ElasticRestClientFactory.
                build(httpHost(), connectTimeOut, socketTimeOut, connectionRequestTime,
                        maxConnectNum, maxConnectPerRoute, userName, password);
    }

    @Bean
    @Scope("singleton")
    public RestClient getRestClient() {
        return getFactory().getClient();
    }

    @Bean
    @Scope("singleton")
    public RestHighLevelClient getRestHighClient() {
        return getFactory().getRestHighClient();
    }

    //-------------------------second-----------------------//
//    @Bean
//    public RestClientBuilder restClientBuilder() {
//        HttpHost[] hosts = Arrays.stream(ipAddrs)
//                .map(this::makeHttpHost)
//                .filter(Objects::nonNull)
//                .toArray(HttpHost[]::new);
//        log.debug("es hosts:{}", Arrays.toString(hosts));
//        return RestClient.builder(hosts);
//    }

//    private HttpHost makeHttpHost(String s) {
//        assert StringUtils.isNotEmpty(s);
//        String[] address = s.split(":");
//        if (address.length == ADDRESS_LENGTH) {
//            String ip = address[0];
//            int port = Integer.parseInt(address[1]);
//            return new HttpHost(ip, port, HTTP_SCHEME);
//        } else {
//            return null;
//        }
//    }

//    @Bean
//    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
//        restClientBuilder.setMaxRetryTimeoutMillis(60000);
////        RestHighLevelClient client = new RestHighLevelClient(
////                RestClient.builder(
////                        new HttpHost("localhost", 9200, "http"),
////                        new HttpHost("localhost", 9201, "http")));
//
//        return new RestHighLevelClient(restClientBuilder);
//    }
}