package org.example.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextConfig {
    @Bean
    @LoadBalanced // 让这个RestTemplate在请求时拥有客户端负载均衡的能力
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
