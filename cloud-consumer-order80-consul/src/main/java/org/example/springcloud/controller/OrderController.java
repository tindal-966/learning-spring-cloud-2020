package org.example.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    private RestTemplate restTemplate;

    @Autowired
    OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${server.port}")
    private String serverPort;

    private static final String SERVER_URL = "http://cloud-provider-payment";

    @GetMapping("/consumer/consul")
    public String zk() {
        return restTemplate.getForObject(String.format("%s/payment/consul", SERVER_URL), String.class);
    }
}
