package org.example.springcloud.service.impl;

import org.example.springcloud.dao.PaymentDao;
import org.example.springcloud.entities.Payment;
import org.example.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentDao paymentDao;

    @Autowired
    PaymentServiceImpl(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Override
    public Payment create(Payment payment) {
        int i = paymentDao.create(payment);
        if (i != 0) {
            return payment;
        }

        return null;
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.getPaymentById(id);
    }
}
