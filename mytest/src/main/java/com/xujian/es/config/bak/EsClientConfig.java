package com.xujian.es.config.bak;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujian on 2019-08-01
 */
@Configuration
public class EsClientConfig {
    @Value("${elasticsearch.ipAddrs}")
    private List<String> nodes;
    @Value("${elasticsearch.client.maxConnectNum}")
    private Integer maxConnectTotal;
    @Value("${elasticsearch.client.maxConnectPerRoute}")
    private Integer maxConnectPerRoute;
    @Value("${elasticsearch.client.connectionRequestTime}")
    private Integer connectionRequestTimeoutMillis;
    @Value("${elasticsearch.client.socketTimeOut}")
    private Integer socketTimeoutMillis;
    @Value("${elasticsearch.client.connectTimeOut}")
    private Integer connectTimeoutMillis;

//    @Bean
//    public RestHighLevelClient getRestHighLevelClient() {
//        List<HttpHost> httpHosts = new ArrayList<>();
//        for (String node : nodes) {
//            try {
//                String[] parts = StringUtils.split(node, ":");
//                Assert.notNull(parts,"Must defined");
//                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
//                httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), "http"));
//            } catch (RuntimeException ex) {
//                throw new IllegalStateException(
//                        "Invalid ES nodes " + "property '" + node + "'", ex);
//            }
//        }
//        return EsClientBuilder.build(httpHosts)
//                .setConnectionRequestTimeoutMillis(connectionRequestTimeoutMillis)
//                .setConnectTimeoutMillis(connectTimeoutMillis)
//                .setSocketTimeoutMillis(socketTimeoutMillis)
//                .setMaxConnectTotal(maxConnectTotal)
//                .setMaxConnectPerRoute(maxConnectPerRoute)
//                .create();
//    }
}
