### SpringCloud 依赖 SpringBoot 的具体版本要求
> 建议参考 SpringCloud 版本的具体说明，会有具体的 SpringBoot 版本指定
https://start.spring.io/actuator/info

### 2020 年 SpringCloud 生态变化
- 服务发现
    - x Eureka
    - Zookeeper
    - Consul
    - Nacos
- 服务调用
    - Ribbon
    - LoadBalancer
- 服务调用2
    - x Feign
    - OpenFeign
- 服务降级
    - x Hystrix
    - resilience4j (国外)
    - sentienl (alibaba)
- 服务网关
    - x Zuul
    - gateway
- 服务配置
    - x Config
    - Nacos
- 服务总线
    - x Bus
    - Nacos

### 添加微服务模块
1. 建 module
2. 改 POM
3. 写 YAML
4. 主启动类
5. 业务类

### 参考仓库，用来复制配置等内容
- [lixiaogou/cloud2020](https://gitee.com/lixiaogou/cloud2020) 比较新
- [cunjinFS/SpringCloud](https://gitee.com/cunjinFS/SpringCloud) 有 SQL, 脑图等资源

### IDEA 设置 Spring Devtools
- [官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)

> 建议先按照官方文档配置，无效之后再按照下面的配置

1. Add devtools to proj
    ``` xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    ```
2. Add plugin to pom.xml

    在 parent pom 文件中添加
    ``` xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.2.2.RELEASE</version>
        <configuration>
            <fork>true</fork>
            <addResources>true</addResources>
        </configuration>
    </plugin>
    ```
3. Enabling automatic build
    「Build, Execution, Deployment -> Compiler」

    - [x] Automatically show first error in editor
    - [x] Display notification on build completion
    - [x] Build project automatically
    - [x] Compile independent modules in parallel
4. Update the value of 
    press `ctrl+shift+Alt+/` and search `Registry`, enable
    - `compiler.automake.allow.when.app.running`
    - `action.System.assertFocusAccessFromEdt`
5. Reboot IDEA

### 几个比较重要的注解
- `@EnableDiscoveryClient` 
- `@LoadBalanced` 负载均衡，貌似和 Ribbon 有关

### Eureka 的自我保护模式

属于 CAP 的 AP。策略是暂时认为服务还是可用的，只是现在的网络出现了问题，网络恢复的之后服务就会恢复
> spring-cloud-starter-netflix-eureka-client 默认配置是 CAP 的 AP
>
> spring-cloud-starter-zookeeper-discovery 默认配置是 CAP 的 CP

参考：
- [https://www.baeldung.com/eureka-self-preservation-renewal](https://www.baeldung.com/eureka-self-preservation-renewal)
- [https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode](https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode)