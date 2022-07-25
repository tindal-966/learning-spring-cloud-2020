package org.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AlibabaConsumerOrderMain83 {
    public static void main(String[] args) {
        SpringApplication.run(AlibabaConsumerOrderMain83.class, args);
    }
}
