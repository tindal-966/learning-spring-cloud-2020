package org.example.springcloud.service;

import org.example.springcloud.entities.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("CLOUD-PAYMENT-SERVICE")
@RequestMapping("/payment")
public interface PaymentService {
    @GetMapping("/getPaymentById/{id}")
    CommonResult getPaymentById(@PathVariable("id") Long id);
}
