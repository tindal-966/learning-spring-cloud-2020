package org.example.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.springcloud.entities.CommonResult;
import org.example.springcloud.entities.Payment;
import org.example.springcloud.lb.MyLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class OrderController {
    private final RestTemplate restTemplate;
    private final MyLoadBalancer myLoadBalancer;
    private final DiscoveryClient discoveryClient;

    @Autowired
    OrderController(RestTemplate restTemplate, MyLoadBalancer myLoadBalancer, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.myLoadBalancer = myLoadBalancer;
        this.discoveryClient = discoveryClient;
    }

    private static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @PostMapping("/payment/create")
    public CommonResult<Payment> create(@RequestBody Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, CommonResult.class);
    }

    @GetMapping("/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PAYMENT_URL + "/payment/getPaymentById/" + id, CommonResult.class);
    }

    @PostMapping("/payment/create/entity")
    public CommonResult<Payment> postForEntity(@RequestBody Payment payment) {
        ResponseEntity<CommonResult> entity = restTemplate.postForEntity(
                PAYMENT_URL + "/payment/create", payment, CommonResult.class);

        if (entity.getStatusCode().is2xxSuccessful()) {
            log.info(entity.getStatusCode() + "\t" + entity.getHeaders() + "\t" + entity.getBody());
            return entity.getBody();
        }

        return new CommonResult<>(entity.getStatusCodeValue(),
                entity.getBody().toString());
    }

    @GetMapping("/payment/getForEntity/{id}")
    public CommonResult<Payment> getForEntity(@PathVariable("id") Long id) {
        ResponseEntity<CommonResult> entity = restTemplate.getForEntity(
                PAYMENT_URL + "/payment/getPaymentById/" + id, CommonResult.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            log.info(entity.getStatusCode() + "\t" + entity.getHeaders() + "\t" + entity.getBody());
            return entity.getBody();
        }

        return new CommonResult<>(entity.getStatusCodeValue(), entity.getBody().toString());
    }

    @GetMapping("/payment/lb")
    public String lb() {
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");

        if (instances == null || instances.isEmpty()) {
            return null;
        }

        ServiceInstance serviceInstance = myLoadBalancer.getServiceInstance(instances);
        return restTemplate.getForObject(serviceInstance.getUri() + "/payment/lb", String.class);
    }
}
