### Diff with Spring Web
> 截至当前
1. Module 基本都需要在 application.yml 中表明自己的 `spring.application.name` 作为服务注册名
2. 添加了服务注册有关依赖以及有关的连接配置
    - spring-boot-starter-actuator 在服务注册中暴露 check health 等接口
    - 注册中心
      - spring-cloud-starter-netflix-eureka-client 使用 Eureka 注册中心
      - spring-cloud-starter-zookeeper-discovery 使用 Zookeeper 注册中心
      - spring-cloud-starter-consul-discovery 使用 Consul 注册中心
3. 需要启动有关的注册中心，Eureka/Zookeeper/Consul（Eureka 注册中心需要自编码并启动）
4. 启动类添加注解
    - `@EnableEurekaClient` Eureka 注册 client 使用
    - `@EnableDiscoveryClient` Zookeeper, Consul 注册服务使用
    - `@EnableHystrix` 启用 Hystrix（或者使用 `@EnableCircuitBreaker`）
5. 服务间调用使用 OpenFeign（内置 Ribbon 支持）
    
    解决了什么问题？服务间调用可以直接使用被调用者的 service 定义（实现类保留 @RequestMapping 变接口 or 接口补充 @RequestMapping 说明请求地址）
    
    新问题：
    1. 启动类只需要添加 `@EnableFeignClients`，无需再指明服务注册中心的类型，所以是怎么判断的？
    2. 服务 interface 的 method 需要带上 `@RequestMapping` 注解，正常编程来说是 interface impl 才指明的，这个时候只能人为联动，容易犯错，有没有更优雅的方式？
6. 添加降级、熔断处理代码 `@HystrixCommand`
    - service-itself 常用方法级别 `@HystrixCommand` 和类级别 `@DefaultProperties(defaultFallback = "method-name")`（类级别需要注意还需要使用 `@HystrixCommand` 指定哪些 method 需要 default fallback）
    - consumer-side 常用 `@FeignClient(fallback = xxx.class)`
7. 部分配置文件放置到 Git 某个仓库中单独管理，使用 SpringCloud Config + Bus 来获取以及动态更新

### SpringCloud 对 SpringBoot 的版本要求
建议参考 SpringCloud 版本的具体说明，会有具体的 SpringBoot 版本指定。另，可参考 [这里](https://start.spring.io/actuator/info)

### 2020 年 SpringCloud 生态变化
- 服务发现
    - x Eureka
    - Zookeeper
    - Consul
    - Nacos
- 服务调用（主要服务调用负载均衡方面）
    - Ribbon
    - LoadBalancer
- 服务调用2（主要服务调用方面）
    - x Feign
    - OpenFeign
- 服务降级
    - x Hystrix
    - resilience4j (国外常用)
    - sentinel (alibaba 开发，国内常用)
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

### Eureka, zookeeper, Consul 异同
| 组件名 | 语言 | CAP | 服务健康检查 | 对外暴露接口 | SpringCloud 集成 |
| -- | --- | --- | --- | --- | --- |
| Eureka | Java | AP | 可配支持 | HTTP | Y |
| Zookeeper | Java | CP | Yes | 客户端 | Y |
| Consul | Go | CP | Yes | HTTP/DNS | Y |

### Eureka 的自我保护模式 self preservation renewal
属于 CAP 的 AP。策略是暂时认为服务还是可用的，只是现在的网络出现了问题，网络恢复的之后服务就会恢复

- spring-cloud-starter-netflix-eureka-client 默认配置是 CAP 的 AP 
- spring-cloud-starter-zookeeper-discovery 默认配置是 CAP 的 CP

参考：
- [https://www.baeldung.com/eureka-self-preservation-renewal](https://www.baeldung.com/eureka-self-preservation-renewal)
- [https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode](https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode)

### Ribbon
LoadBalance 的四种方式：
1. 客户端
2. 服务端
3. 集中式
4. 进程内

Ribbon 属于 *客户端+进程内*，Nginx 属于 *服务端+集中式*

Ribbon 负载均衡算法自带实现查看 `IRule` 接口，抽象实现 `AbstractLoadBalancerRule`
- RoundRobinRule 轮询
- RandomRule
- RetryRule 先 Round ，获取失败则在有限时重试
- WeightedResponseTimeRule(extends RoundRobinRule) 选择响应速度快的
- BestAvailableRule 过滤断路的，选并发最小的
- AvailabilityFilteringRule 先过滤故障的，选并发小的
- ZoneAvoidanceRule 判断 service 所在区域的性能和可用性来选择（默认）

### OpenFeign 超时
OpenFeign 的超时由内置的 Ribbon 控制，默认 1s，设置
``` yml
ribbon:
  #指的是建立连接所用的时间，适用于网络状况正常的情况下, 两端连接所用的时间
  ReadTimeout: 5000
  #指的是建立连接后从服务器读取到可用资源所用的时间
  ConnectTimeout: 5000
```

### Hystrix
作用：
- Fallback 降级
    
    降级的维度：
    - 对 service 自己的降级（`@HystrixCommand` 实现），包括 provider, consumer 对自己降级保护 
    - 对 provider 的降级（在 `@FeignClient` 中指明 fallback 属性）
- CircuitBreak 熔断
- FlowLimit 限流

Hystrix 的三个功能都可以实现 provider-side 和 consumer-side 的控制

### 服务间称呼
provider, consumer 是有意义的。如果使用 client, server 容易混乱，因为 client 也是一个 service

### @FeignClient & class-level @RequestMapping 问题
在 interface 同时使用 `@FeignClient` 和 `@RequestMapping` ，而且在有该接口的实现类时可能会引发 **Ambiguous mapping.** 问题
> 基于 spring-boot-starters:2.2.2.RELEASE 和 spring-cloud-openfeign:2.2.1.RELEASE

大致说明：Spring 旧版本会将带有 `@RequestMapping` 注解的接口/类注册为 HandlerMethods，导致后续真正需要注册的实现类冲突（顺序也有可能反过来，总之就是重复注册）

解决：
- 使用 `@FeignClient` 的 path 替代 class-level 的 `@RequestMapping`
- 将 class-level 的 `@RequestMapping` 全部复制到 method 中
- 按照 参考 的第一个链接做修改

参考：
- [中文的代码分析](https://blog.csdn.net/aileitianshi/article/details/95980329)
- [spring-cloud-netflix github issues 上的讨论](https://github.com/spring-cloud/spring-cloud-netflix/issues/466)
- [Spring-framework milestone:6.0M1 已解决这个问题，不会再注册只有 @RequestMapping 的接口/类](https://github.com/spring-projects/spring-framework/issues/22154#issuecomment-936906502)
- [spring-cloud-openfeign 建议不要这样使用（截至 v3.1.3 都提示不支持）](https://github.com/spring-cloud/spring-cloud-openfeign/issues/678)
    > [v3.1.3 doc](https://docs.spring.io/spring-cloud-openfeign/docs/3.1.3/reference/html/#spring-cloud-feign-inheritance)

补充：Spring Web 新建 Controller 新建接口并添加 `@RequestMapping` 注解导致的一些问题
> 不知道和上面的有没有关系，还是说只是单纯是因为实现类继承了接口的 `@RequestMapping` 注解
``` java
// 依赖：
//<dependency>
//    <groupId>org.springframework.boot</groupId>
//    <artifactId>spring-boot-starter-web</artifactId>
//    <version>2.2.2.RELEASE</version>
//</dependency>

// Controller 类：
@RestController
public class Controller implements ControllerInterface {
    @GetMapping("/hello")
    public String greeting() {
        return "hello, world";
    }
}

// ControllerInterface 类：
@RequestMapping("/i")
public interface ControllerInterface {
    @GetMapping("/hello")
    String greeting();
}
```
此时，调用 `/hello` 404，调用 `/i/hello` 正常
- 如果 Controller 也添加 `@RequestMapping("/i")`，调用 `/i/hello` 正常
- 如果 Controller 添加 `@RequestMapping("/other")`，调用 `/other/hello` 正常，调用 `/i/hello` 404

### SpringCloud Gateway
依赖：`spring-cloud-starter-gateway`

核心概念：
- Route 路由
- Predicate 断言（断言为 true 则路由）
- Filter 过滤器（筛选请求、修改请求）

疑问：
- Gateway 一般用于什么场景？貌似和 Ribbon 负载均衡有一定的重合。所有微服务集合的最外层？

### SpringCloud Config + Bus
实现配置信息的 集中管理 + 动态更新

依赖：
- config server: `spring-cloud-config-server`
- config client: `spring-cloud-starter-config`
- bus: `spring-cloud-starter-bus-amqp`

- SpringCloud Config 集中化的外部配置中心
- SpringCloud Bus 利用消息总线更新配置

BUS 的两种触发更新的方式：
- 利用 BUS 触发一个客户端 /bus/refresh，而刷新所有客户端的配置（刷新客户端，传染其他客户端）
  
    POST http://localhost:xxx/actuator/refresh
- 利用 BUS 触发服务端 ConfigService 的 /bus-refresh，而刷新所有客户端的配置（刷新服务端，通知客户端）

    GET http://localhost:xxx/actuator/bus-refresh</service-name:service-port> 支持全部更新以及指定个别微服务更新

### bootstrap.yml VS application.yml
application.yml 用户级的资源配置项
bootstrap.yml 系统级的，优先级更高

SpringCloud 会创建一个 `Bootstrap Context`，作为 Spring 应用的 `Application Context` 的父上下文。
初始化的时候，Bootstrap Context 负责从外部源加载配置属性并解析配置。这两个上下文共享一个从外部获取的 Environment

### SpringCloud Stream
解决问题：屏蔽底层消息中间件的差异，降低切换成本，统一消息的编程模型（类似 JDBC）

核心概念：
- Binder 连接中间件，屏蔽差异
- Channel 通道，是 Queue 的抽先，在消息通讯系统中就是存储和转发的媒介，通过 Channel 对队列进行配置
- Source/Sink 从 Stream 发布消息就是输出，接受消息就是输入

疑问：
- 当前的 Provider 只在项目的配置文件中指明 exchange name，假设项目存在多个 service 都往这个 exchange 中发？Route key 这些怎么配置？