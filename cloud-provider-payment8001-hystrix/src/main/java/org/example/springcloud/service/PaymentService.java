package org.example.springcloud.service;

import cn.hutool.core.util.IdUtil;
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

    /* Demo1: Fallback */
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

    /* Demo2: CircuitBreak */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerFallback", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"), // 更多 Property 参考类 HystrixCommandProperties 或 wiki: https://github.com/Netflix/Hystrix/wiki/Configuration
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"), // default => statisticalWindowVolumeThreshold: 20 requests in 10 seconds must occur before statistics matter
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"), // default => sleepWindow: 5000 = 5 seconds that we will sleep before trying again after tripping the circuit
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60") // default => errorThresholdPercentage = 50 = if 50%+ of requests in 10 seconds are failures or latent then we will trip the circuit
    })
    public String paymentCircuitBreaker(Integer id) {
        if (id < 0) {
            throw new RuntimeException("******id 不能负数");
        }

        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t" + "调用成功，流水号: " + serialNumber;
    }

    public String paymentCircuitBreakerFallback(Integer id) {
        return Thread.currentThread().getName() + "\t" + "id 不能负数或超时或自身错误，请稍后再试，/(ㄒoㄒ)/~~   id: " + id;
    }
}
