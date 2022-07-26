package org.example.springcloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"org.example.springcloud.dao"})
public class MyBatisConfig {
}
