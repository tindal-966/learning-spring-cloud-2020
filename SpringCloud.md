### Spring Cloud 依赖 Spring Boot 的具体版本要求
https://start.spring.io/actuator/info

### 2020 年 SpringCloud 生态变化
- 服务发现
    - x Ereku
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