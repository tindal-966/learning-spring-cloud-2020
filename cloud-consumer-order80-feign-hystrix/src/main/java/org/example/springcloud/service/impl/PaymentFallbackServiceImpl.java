package org.example.springcloud.service.impl;

import org.example.springcloud.service.PaymentService;
import org.springframework.stereotype.Component;

@Component
public class PaymentFallbackServiceImpl implements PaymentService {
    @Override
    public String paymentInfoOK(Integer id) {
        return "org.example.springcloud.service.impe.PaymentFallbackServiceImpl.paymentInfoOK error.";
    }

    @Override
    public String paymentInfoTimeOut(Integer id) {
        return "org.example.springcloud.service.impe.PaymentFallbackServiceImpl.paymentInfoTimeOut error";
    }
}
