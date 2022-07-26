package org.example.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.example.springcloud.entities.CommonResult;
import org.example.springcloud.entities.Payment;
import org.example.springcloud.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class CircleBreakerController {
    @Resource
    private PaymentService paymentService;

    @GetMapping(value = "/consumer/paymentSQL/{id}")
    public CommonResult<Payment> paymentSQL(@PathVariable("id") Long id) {
        return paymentService.paymentSQL(id);
    }
    
    // ------ 测试 fallbackHandler/blockHandler ------
    @RequestMapping("/consumer/nothing/{id}")
    @SentinelResource(value = "testingRule") //没有配置
    public CommonResult<Payment> nothing(@PathVariable Long id) {
        return innerMethod(id);
    }

    @RequestMapping("/consumer/fallback/{id}")
    @SentinelResource(value = "testingRule", fallback = "handlerFallback") // fallback 只负责业务异常
    public CommonResult<Payment> justFallback(@PathVariable Long id) {
        return innerMethod(id);
    }

    @RequestMapping("/consumer/block/{id}")
    @SentinelResource(value = "testingRule",blockHandler = "blockHandler") // blockHandler 只负责 sentinel控制台配置违规（控制台可以建议设置异常数来测试）
    public CommonResult<Payment> justBlock(@PathVariable Long id) {
        return innerMethod(id);
    }

    @RequestMapping("/consumer/both/{id}")
    @SentinelResource(value = "testingRule", fallback = "handlerFallback", blockHandler = "blockHandler", // fallback, block
            exceptionsToIgnore = {IllegalArgumentException.class}) // 还可以额外设置忽略的异常
    public CommonResult<Payment> both(@PathVariable Long id) {
        return innerMethod(id);
    }

    // 本例是fallback
    public CommonResult handlerFallback(@PathVariable Long id, Throwable e) {
        Payment payment = new Payment(id, "null");
        return new CommonResult<>(444, "兜底异常handlerFallback,exception内容  " + e.getMessage(), payment);
    }

    // 本例是blockHandler
    public CommonResult blockHandler(@PathVariable Long id, BlockException blockException) {
        Payment payment = new Payment(id, "null");
        return new CommonResult<>(445, "blockHandler-sentinel限流,无此流水: blockException  " + blockException.getMessage(), payment);
    }
    
    private CommonResult<Payment> innerMethod(Long id) {
        CommonResult<Payment> result = paymentService.paymentSQL(id);
        
        if (id == 4) {
            throw new IllegalArgumentException("IllegalArgumentException,非法参数异常....");
        } else if (result.getData() == null) {
            throw new NullPointerException("NullPointerException,该ID没有对应记录,空指针异常");
        }

        return result;
    }
}