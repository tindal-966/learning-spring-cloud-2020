package org.example.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.springcloud.entities.CommonResult;
import org.example.springcloud.entities.Payment;
import org.example.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/create")
    public CommonResult create(Payment payment) {
        int i = paymentService.create(payment);

        if (i != 0) {
            log.info("创建成功");
            return new CommonResult(200, "success");
        }

        return new CommonResult(500, "failed");
    }

    @GetMapping("/getPaymentById/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment paymentById = paymentService.getPaymentById(id);

        if (paymentById != null) {
            return new CommonResult(200, "success", paymentById);
        }

        log.info("获取失败，ID:" + id);
        return new CommonResult(400, "failed", null);
    }

}
