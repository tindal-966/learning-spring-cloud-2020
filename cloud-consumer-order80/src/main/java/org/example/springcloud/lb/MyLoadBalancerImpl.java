package org.example.springcloud.lb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class MyLoadBalancerImpl implements MyLoadBalancer {

    private final AtomicInteger requestTimes = new AtomicInteger(0);

    public final int getAndIncrement() {
        int next = 0;
        int current;

        do {
            current = requestTimes.get();
            next = (current == Integer.MAX_VALUE ? 0: current + 1);
        } while (!requestTimes.compareAndSet(current, next));

        log.info("RequestTimes: " + requestTimes);
        return next;
    }

    @Override
    public ServiceInstance getServiceInstance(List<ServiceInstance> serviceInstanceList) {
        return serviceInstanceList.get(getAndIncrement() % serviceInstanceList.size());
    }
}
