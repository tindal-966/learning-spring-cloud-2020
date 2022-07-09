package org.example.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRibbonRule {

    // 需要与 @ComponentScan 所在的目录下的包分开，具体参考：https://docs.spring.io/spring-cloud-netflix/docs/2.2.9.RELEASE/reference/html/#spring-cloud-ribbon
    @Bean
    public IRule myRule() {
        // 此处将ribbon默认使用的轮询策略改为随机策略
        return new RandomRule();
    }
}
