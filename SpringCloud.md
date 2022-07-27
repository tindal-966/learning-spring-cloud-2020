### Diff with Spring Web
> 截至当前
1. Module 基本都需要在 application.yml 中表明自己的 `spring.application.name` 作为服务注册名
2. 添加了服务注册有关依赖以及有关的连接配置
    - spring-boot-starter-actuator 在服务注册中暴露 check health 等接口
    - 注册中心
      - spring-cloud-starter-netflix-eureka-client 使用 Eureka 注册中心
      - spring-cloud-starter-zookeeper-discovery 使用 Zookeeper 注册中心
      - spring-cloud-starter-consul-discovery 使用 Consul 注册中心
      - spring-cloud-starter-alibaba-nacos-discovery 使用 Alibaba Nacos 作为注册中心
3. 需要启动有关的注册中心，Eureka/Zookeeper/Consul/Nacos（Eureka 注册中心需要自编码并启动）
4. 启动类添加注解
    - `@EnableDiscoveryClient` Eureka, Zookeeper, Consul, Nacos 注册服务使用（Eureka 还可以使用 `@EnableEurekaClient`）
    - 其他
      - `@EnableFeignClients` 启用 OpenFeign 服务间调用
      - `@EnableHystrix` 启用 Hystrix（或者使用 `@EnableCircuitBreaker`）
5. 服务间调用使用 OpenFeign（内置 Ribbon 支持），`@FeignClient`
    
    解决了什么问题？服务间调用可以直接使用被调用者的 service 定义（实现类保留 @RequestMapping 变接口 or 接口补充 @RequestMapping 说明请求地址）

   疑问：
    1. 启动类只需要添加 `@EnableFeignClients`，无需再指明服务注册中心的类型，所以是怎么判断的？
    2. 服务 interface 的 method 需要带上 `@RequestMapping` 注解，正常编程来说是 interface impl 才指明的，这个时候只能人为联动，容易犯错，有没有更优雅的方式？
6. 添加降级、熔断处理代码
    1. Hytrix `@HystrixCommand`
       - service-itself 常用方法级别 `@HystrixCommand` 和类级别 `@DefaultProperties(defaultFallback = "method-name")`（类级别需要注意还需要使用 `@HystrixCommand` 指定哪些 method 需要 default fallback）
       - consumer-side 常用 `@FeignClient(fallback = xxx.class)`
    2. Sentinel `@SentinelResource`，注意 fallbackHandler 和 blockHandler 的区别
7. 部分配置文件放置到 Git 某个仓库中单独管理，使用 SpringCloud Config + Bus 来获取以及动态更新 `@@EnableBinding`
8. 微服务添加链路跟踪有关依赖和配置
    - 依赖：spring-cloud-starter-zipkin
    - 配置：spring.ziplin, spring.sleuth

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

### Eureka, zookeeper, Consul, Nacos 异同
| 组件名 | 语言 | CAP | 服务健康检查 | 对外暴露接口 | SpringCloud 集成 |
| -- | --- | --- | --- | --- | --- |
| Eureka | Java | AP | 可配支持 | HTTP | Y |
| Zookeeper | Java | CP | Yes | 客户端 | Y |
| Consul | Go | CP | Yes | HTTP/DNS | Y |
| Nacos | Java | AP/CP | - | - | Y |

### Eureka 的自我保护模式 self preservation renewal
属于 CAP 的 AP。策略是暂时认为服务还是可用的，只是现在的网络出现了问题，网络恢复的之后服务就会恢复

- spring-cloud-starter-netflix-eureka-client 默认配置是 CAP 的 AP 
- spring-cloud-starter-zookeeper-discovery 默认配置是 CAP 的 CP

参考：
- [https://www.baeldung.com/eureka-self-preservation-renewal](https://www.baeldung.com/eureka-self-preservation-renewal)
- [https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode](https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode)

### Ribbon 服务负载均衡
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

### Hystrix 服务降级、熔断、限流
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

    是的，用在所有微服务的最外层。因为如果前端需要调用微服务，不可能知道具体哪个接口对应哪个微服务，哪个微服务对应哪个端口，所以前端是直接调用网关的端口
- 网关是怎么转发路由的（就什么都不配，就直接启动一个网关）？直接抹除端口按照 URI 来转发？如果不同类型的微服务（不是横向拓展）的 URI 相同，此时端口不同，是怎么处理的？

    什么都不配是不可行的，需要配置 **特定的 URI 对应的服务 ID** （因为使用了注册中心，所以直接服务 ID 即可，横向拓展、端口号这些都由注册中心解决）。一个 Zuul 的配置示例：
    ``` yml
    zuul:
        routes:
            account:
                  path: /restful/accounts/**
                  serviceId: account
                  stripPrefix: false
                  sensitiveHeaders: "*"
    ```

### SpringCloud Config + Bus 配置动态更新
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

> 虽然 SpringCloud Config + Bus 依赖 Git 作为配置中心有点麻烦，但好像比 Nacos 直接页面配置或者 MySQL 保存在历史版本和迁移方面更有优势

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

注解：
- `@EnableBinding` 指信道 channel 和 exchange 绑定在一起
- `@StreamListener` 监听队列，用于消费者的队列消息接收

注意：
- Stream 中同一个 Group 的多个消费者是竞争关系（最终只有一个消费者可以消费到消息），不同组的是可以全面消费的（即重复消费）（一个生产者，多个消费者的情况下，如果消费者们没有在分组处理，则默认是不同组会重复消费）
- Group 属性在消息持久话方面也有作用，所以建议默认配置为对应的服务名，例如：`spring.cloud.stream.bindings.input.group: ${spring.application.name}`

疑问：
- 当前的 Provider 只在项目的配置文件中指明 exchange name，假设项目存在多个 service 都往这个 exchange 中发？Route key 这些怎么配置？
- 按照当前的类似 RPC 的调用方式真正的使用场景是什么？

### SpringCloud Sleuth 链路追踪（Sleuth, 侦探）
一般结合 Zipkin 使用

依赖：`spring-cloud-starter-zipkin` 内置 Sleuth

原理：
一条链路通过 Trace ID 唯一标识，Span 标识发起的请求信息，各 Span 通过 parent id 关联起来。例如：

### SpringCloud Alibaba
[Github](https://github.com/alibaba/spring-cloud-alibaba) & [Spring.io](https://spring.io/projects/spring-cloud-alibaba)

主要功能：
- 服务限流降级：默认支持 WebServlet、WebFlux、OpenFeign、RestTemplate、Spring Cloud Gateway、Zuul、Dubbo 和 RocketMQ 限流降级功能的接入，可以在运行时通过控制台实时修改限流降级规则，还支持查看限流降级 Metrics 监控。
- 服务注册与发现：适配 Spring Cloud 服务注册与发现标准，默认集成了 Ribbon 的支持。
- 分布式配置管理：支持分布式系统中的外部化配置，配置更改时自动刷新。
- 消息驱动能力：基于 Spring Cloud Stream 为微服务应用构建消息驱动能力。
- 分布式事务：使用 @GlobalTransactional 注解， 高效并且对业务零侵入地解决分布式事务问题。
- 分布式任务调度：提供秒级、精准、高可靠、高可用的定时（基于 Cron 表达式）任务调度服务。同时提供分布式的任务执行模型，如网格任务。网格任务支持海量子任务均匀分配到所有 Worker（schedulerx-client）上执行。
- 阿里云对象存储：阿里云提供的海量、安全、低成本、高可靠的云存储服务。支持在任何应用、任何时间、任何地点存储和访问任意类型的数据。
- 阿里云短信服务：覆盖全球的短信服务，友好、高效、智能的互联化通讯能力，帮助企业迅速搭建客户触达通道。

### Alibaba Nacos & Sentinel & Seata
- Nacos [Github](https://github.com/alibaba/nacos) & [website](https://nacos.io)
- Sentinel [Github](https://github.com/alibaba/sentinel) & [website](https://sentinel.io)
- Seata [Github](https://github.com/alibaba/seata) & [website](https://seata.io)

Nacos 服务发现实例模型：
- 临时实例（Eureka, Zookeeper）
  - 客户端上传健康状态
  - 摘除不健康状态
  - 非持久化
- 持久化实例（Consul, CoreDns）
  - 服务端探测健康状态
  - 保留不健康实例
  - 持久化

Nacos 特性对比（截至 2020 年）：
| | Nacos | Eureka | Consul | CoreDns | Zookeeper |
| --- | --- | --- | --- | --- | --- |
| 一致性协议 | CP + AP | AP | CP | - | CP |
| 健康检查 | TCP/HTTP/MySQL/Client Beat | Client Beat | TCP/HTTP/gRPC/Cmd | - | Client Beat |
| 负载均衡 | 权重/DSL/metadata/CMDB | Ribbon | Fabio | RR | - |
| 雪崩保护 | Y | Y | N | N | N |
| 自动注销实例 | Y | Y | N | N | Y |
| 访问协议 | HTTP/DNS/UDP | HTTP | HTTP/DNS | DNS | TCP |
| 监听支持 | Y | Y | Y | N | Y |
| 多数据中心 | Y | Y | Y | N | N |
| 跨注册中心 | Y | N | Y | N | N |
| SpringCloud 集成 | Y | Y | Y | N | N |
| Dubbo 集成 | Y | N | N | N | N |
| K8s 集成 | Y | N | Y | N | N |

Sentinel 主要功能：
- 流量控制
- 熔断降级
- 热点参数限流
- 系统自适应保护（系统级别规则）
> 分别对应 Dashboard 上面的 流控、降级、热点、系统规则

Seata 核心概念：
- Transaction ID, XID 全局唯一的事务 ID
- TC (Transaction Coordinator) - 事务协调者（一般来说就是 Seata 服务器）
- TM (Transaction Manager) - 事务管理器（一般来说就是标注 @GlobalTransactional 的方法，是事务的发起方）
- RM (Resource Manager) - 资源管理器（一般来说就是方法内涉及到的数据源，是事务的参与方）

Seata 执行流程：
1. TM 向 TC 申请开启一个全局事务，全局事务创建成功并生成一个全局唯一的 XID
2. XID 在微服务调用链路的上下文中传播
3. RM 向 TC 注册分支事务，将其纳入 XID 对应的全局事务的管辖（RM 向 TC 汇报资源准备状态）
4. TM 向 TC 发起针对 XID 的全局提交或回滚决议（此时，TM 结束分布式事务，事务一阶段结束，TM 通知 TC 事务结果：提交或回滚）
5. TC 调度 XID 下管辖的全部分支事务完成提交或回滚提交（TC 通知所有 RM 去提交或者回滚，事务二阶结束）

Seata 四种模式：
- AT
- TCC
- SAGA
- XA

Seata [快速开始](http://seata.io/zh-cn/docs/user/quickstart.html) & [AT 模式](http://seata.io/zh-cn/docs/dev/mode/at-mode.html)