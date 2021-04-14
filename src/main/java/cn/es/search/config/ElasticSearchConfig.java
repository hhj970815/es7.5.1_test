package cn.es.search.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * elasticsearch配置类
 *
 * @author haunghuajie
 * @date 2020/08/06
 */
@Component
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
@Slf4j
public class ElasticSearchConfig {

    @Value("${elasticsearch.hostname}")
    private String hostName;
    @Value("${elasticsearch.port}")
    private Integer port;

    @Bean
    @Qualifier("restHighLevelClient")
    public RestHighLevelClient initRestHighLevelClient() {
        log.info("实例化ElasticSearch客户端-{}-{}", hostName, port);
        // 实例化客户端
        HttpHost httpHost = new HttpHost(hostName, port);
        RestClientBuilder builder = RestClient.builder(httpHost);
        return new RestHighLevelClient(builder);
    }
}
