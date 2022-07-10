package org.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker // 注解开启断路器功能
public class PaymentMain8001Hystrix {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8001Hystrix.class, args);
    }
}
