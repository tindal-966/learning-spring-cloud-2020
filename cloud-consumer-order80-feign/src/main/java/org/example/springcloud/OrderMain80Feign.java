package org.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderMain80Feign {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain80Feign.class, args);
    }
}
