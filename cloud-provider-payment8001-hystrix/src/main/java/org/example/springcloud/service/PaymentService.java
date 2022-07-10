package org.example.springcloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {
    public String paymentInfo_OK(Integer id) {
        return "线程池:  " + Thread.currentThread().getName() + "  paymentInfoOK,id:  " + id + "\t"
                + "O(∩_∩)O哈哈~";
    }

    @HystrixCommand(fallbackMethod = "paymentInfoTimeOutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    public String paymentInfo_TimeOut(Integer id) {
        int timeout = 3;
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "线程池:  " + Thread.currentThread().getName() + "  paymentInfoTimeOut,id:  " + id + "\t"
                + "O(∩_∩)O哈哈~" + "  耗时(秒): " + timeout;
    }

    public String paymentInfoTimeOutHandler(Integer id) {
        return "线程池:  " + Thread.currentThread().getName() + "  系统繁忙或者运行报错，请稍后再试,id:  " + id + "\t"
                + "o(╥﹏╥)o";
    }
}
