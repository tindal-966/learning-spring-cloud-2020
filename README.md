## learning-spring-cloud-2020 

### 概述
- 来源：[尚硅谷-Spring Cloud 2020-周阳](https://www.bilibili.com/video/BV18E411x7eT)
- 总结：[SpringCloud.md](./SpringCloud.md)

### 项目目录（按照建立顺序）
- cloud-provider-payment8001 提供者 payment Eureka 服务注册
- cloud-consumer-order80 消费者 order Eureka 服务注册
- cloud-api-commons 实体类公共模块（从 cloud-provider-payment8001 和 cloud-consumer-order80 抽出的公共模块）
- cloud-eureka-server7001 Eureka 服务
- cloud-eureka-server7002 Eureka 服务（与 7001 一致，为了演示 Eureka 的集群）
- cloud-provider-payment8002 提供者 payment Eureka 服务注册
- cloud-provider-payment8004-zk 提供者 payment zookeeper 服务注册
- cloud-consumer-order80-zk 消费者 order Zookeeper 服务注册
- cloud-provider-payment8006-consul 提供者 payment consul 服务注册
- cloud-consumer-order80-consul 消费者 order Consul 服务注册
- cloud-consumer-order80-feign 消费者 order Feign 调用
- cloud-consumer-order80-feign-hystrix 消费者 order Feign 调用，Hystrix 降级
- cloud-provider-payment8001-hystrix 提供者 payment Hystrix 服务降级
- cloud-consumer-hystrix-dashboard9001 Hystrix Dashboard Demo
- cloud-gateway-gateway9527 SpringCloud Gateway 网关
- cloud-config-center-3344 SpringCloud Config 服务端
- cloud-config-client-3355 SpringCloud Config 客户端
- cloud-config-client-3366 SpringCloud Config 客户端（与 3355 一致，为了演示同时通过 RabbitMQ 更新配置文件）
- cloud-stream-rabbitmq-provider8801 SpringCloud Stream 提供者
- cloud-stream-rabbitmq-provider8802 SpringCloud Stream 消费者
- cloud-stream-rabbitmq-consumer8803 SpringCloud Stream 消费者（和 8802 一致，为了演示分组的设置防止消息重复消费）

### 参考
- [lixiaogou/cloud2020](https://gitee.com/lixiaogou/cloud2020) 比较新
- [cunjinFS/SpringCloud](https://gitee.com/cunjinFS/SpringCloud) 有 SQL, 脑图等资源