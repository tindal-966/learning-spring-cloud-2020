### 项目目录
- cloud-api-commons 实体类公共模块
- cloud-consumer-order80 消费者 order Eureka 服务注册
- cloud-consumer-order80-zk 消费者 order zookeeper 服务注册
- cloud-eureka-server7001 Eureka 服务
- cloud-eureka-server7002 Eureka 服务
- cloud-provider-payment8001 提供者 payment Eureka 服务注册
- cloud-provider-payment8002 提供者 payment Eureka 服务注册
- cloud-provider-payment8004-zk 提供者 payment zookeeper 服务注册
- cloud-provider-payment8004-consul 提供者 payment consul 服务注册

### Diff with Spring Web
> 截至当前
1. Module 基本都需要表明自己的 `spring.application.name` 作为服务注册名
2. 添加了服务注册有关依赖以及有关的连接配置
    - spring-boot-starter-actuator 在服务注册中暴露 check health 接口
    - 注册中心
      - spring-cloud-starter-netflix-eureka-client 使用 Eureka 注册中心
      - spring-cloud-starter-zookeeper-discovery 使用 Zookeeper 注册中心
      - spring-cloud-starter-consul-discovery 使用 Consul 注册中心
3. 需要启动有关的注册中心，Eureka/Zookeeper/Consul（Eureka 注册中心需要自编码并启动）
4. Module 启动类添加注解
    - `@EnableEurekaClient` Eureka 注册 client 使用
    - `@EnableDiscoveryClient` Zookeeper, Consul 注册服务使用
5. 服务间调用使用 OpenFeign（内置 Ribbon 支持。使用需要先抽服务接口，开始像 Dubbo 了）
    
    解决了什么问题？服务间调用可以复用和直接使用 service interface，直接添加有关注解即可
    
    新问题：
    1. 启动类只需要添加 `@@EnableFeignClients`，无需再指明服务注册中心的类型，所以是怎么判断的？
    2. 服务 interface 需要带上 `@RequestMapping` 说明，正常编程来说是 interface impl 才指明的，这个时候应该两个都需要了，只能人为协调，有没有更优雅的方式？


### SpringCloud 对 SpringBoot 的版本要求
建议参考 SpringCloud 版本的具体说明，会有具体的 SpringBoot 版本指定。另，可参考 [这里](https://start.spring.io/actuator/info)

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
    - resilience4j (国外常用)
    - sentienl (alibaba 开发，国内常用)
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

### Eureka, zookeeper, Consul 异同
| 组件名 | 语言 | CAP | 服务健康检查 | 对外暴露接口 | SpringCloud 集成 |
| -- | --- | --- | --- | --- | --- |
| Eureka | Java | AP | 可配支持 | HTTP | Y |
| Zookeeper | Java | CP | Yes | 客户端 | Y |
| Consul | Go | CP | Yes | HTTP/DNS | Y |

### Ribbon
Load Balance 的四种方式：
1. 客户端
2. 服务端
3. 集中式
4. 进程内

Ribbon 属于 *客户端+进程内*，Nginx 属于 *服务端+集中式*

Ribbon 负载均衡算法自带实现查看 `IRule` 接口，抽象实现 `AbstractLoadBalancerRule`
- RoundRobinRule
- RandomRule
- RetryRule 先 Round ，获取失败则在有限时重试
- WeightedResponseTimeRule(extends RoundRobinRule) 选择响应速度快的
- BestAvailableRule 过滤断路的，选并发最小的
- AvailabilityFilteringRule 先过滤故障的，选并发小的
- ZoneAvoidanceRule 判断 service 所在区域的性能和可用性来选择（默认）