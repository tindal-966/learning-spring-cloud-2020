package org.example.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.springcloud.entities.CommonResult;
import org.example.springcloud.entities.Payment;
import org.example.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private PaymentService paymentService;

    @Autowired
    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Value("${server.port}")
    private String serverPort;

    @PostMapping("/create")
    public CommonResult create(@RequestBody Payment payment) {
        Payment payment1 = paymentService.create(payment);

        if (payment1 != null) {
            log.info("创建成功");
            return new CommonResult(200, "success" + serverPort, payment1);
        }

        return new CommonResult(500, "failed");
    }

    @GetMapping("/getPaymentById/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment paymentById = paymentService.getPaymentById(id);

        if (paymentById != null) {
            return new CommonResult(200, "success" + serverPort, paymentById);
        }

        log.info("获取失败，ID:" + id);
        return new CommonResult(400, "failed", null);
    }

    @GetMapping("lb")
    public String lb() {
        return serverPort;
    }
}
