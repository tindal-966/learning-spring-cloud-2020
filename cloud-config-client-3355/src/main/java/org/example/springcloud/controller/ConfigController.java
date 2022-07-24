package org.example.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现热加载 自动获取刷新内容
 */
@RestController
@RefreshScope
public class ConfigController {

    @Value("${config.info}") // 这个是 config server 中某个（具体哪个取决与本项目配置文件的配置，label/name-profile.yml）配置文件的一个属性
    private String configInfo;

    @GetMapping("/configInfo")
    public String getConfigInfo() {
        return configInfo;
    }
}
