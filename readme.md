### RabbitMQ 安装
MacOS 安装 rabbitmq
```shell
brew install rabbitmq
```
查看 rabbitmq 安装信息
```shell
brew info rabbitmq
```
启动 rabbitmq 服务，默认端口为 `5672`
```shell
brew services start rabbitmq
```
停止 rabbitmq 服务
```shell
brew services stop rabbitmq
```
开启 rabbitmq 的 web 监控台，rabbitmq 的 web 监控台默认的端口为 `15672`，默认账号密码均为 `guest`
```shell
$RABBITMQ_HOME/sbin/rabbitmq-plugins enable rabbitmq_management
```  

### RabbitMQ Messaging Model
- A producer is a user application that sends messages.
- An exchange is a very simple thing. On one side it receives messages from producers and the other side it pushes them to queues.The exchange must know exactly what to do with a message it receives. Should it be appended to a particular queue? Should it be appended to many queues? Or should it get discarded. The rules for that are defined by the exchange type.
  - <span id="exchange-type-fanout">fanout</span> : It broadcasts all the messages it receives to all the queues it knows
  - <span id="exchange-type-direct">direct</span> : It forwards the message to the queues whose binding key exactly matches the routing key of the message
  - <span id="exchange-type-topic">topic</span>
  - <span id="exchange-type-headers">headers</span>
- A queue is a buffer that stores messages.
- A consumer is a user application that receives messages.

### RabbitMQ 工作模式
#### 简单模式 - Hello World
> The simplest thing that does something\
> https://rabbitmq.com/tutorials/tutorial-one-java.html

生产者生产消息，经由默认交换机发送到 **一个** 队列中，**一个** 消费者从该队列中接收消息\
![简单模式](https://rabbitmq.com/img/tutorials/python-one-overall.png)

#### 工作队列模式 - Work Queue
> Distributing tasks among workers (the competing consumers pattern)\
> https://rabbitmq.com/tutorials/tutorial-two-java.html

生产者生产多条消息，消息经由交换机发送到 **一个** 队列中，**多个** 消费者从该队列中争抢获取消息，相当于将这些消息 **分发** 给多个消费者，该模式下， **一条** 消息只会发给 **一个** 消费者\
![工作队列模式](https://rabbitmq.com/img/tutorials/python-two.png)

#### 发布订阅模式 - Publish / Subscribe
> Sending messages to many consumers at once\
> https://rabbitmq.com/tutorials/tutorial-three-java.html

生产者生产消息，消息经由 **[fanout](#exchange-type-fanout)** 类型的交换机发送到 **所有** 与该交换机绑定的队列中，**订阅** 了该队列的所有消费者都从队列中获取 **同一条** 消息，该模式下，**同一条** 消息会发给 **多个** 消费者\
![订阅发布模式](https://rabbitmq.com/img/tutorials/python-three-overall.png)

#### 路由模式 - Routing
> Receiving messages selectively\
> https://rabbitmq.com/tutorials/tutorial-four-java.html

生产者发送消息到 **[direct](#exchange-type-direct)** 类型的交换机中，交换机根据消息的 routingKey，在与之绑定的队列中筛选出 bindingKey 和 routingKey 匹配的队列，将消息转发到这些队列中，该模式下，绑定在相同 direct 类型交换机上的队列，如果 bindingKey 相同，则会收到相同的消息\
![路由模式](https://rabbitmq.com/img/tutorials/python-four.png)

#### 主题模式 - Topic
> Receiving messages based on a pattern (topics)\
> https://www.rabbitmq.com/tutorials/tutorial-five-java.html

生产者发送消息到 **[topic](#exchange-type-topic)** 类型的交换机中，交换机根据消息的 routingKey，在与之绑定的队列中筛选出 bindingKey 和 routingKey 匹配的队列，与路由模式不同的是，在主题模式下，bindingKey 不在是简单的字符串，而是以 . 分隔的单词组，并且可以使用 **通配符** 代替单词
- `*` 匹配 **一个** 单词
- `#` 匹配 **零个或多个** 单词\
eg : 假设系统日志可以用 `<facility>.<severity>` 来描述，那么当我们指定 bindingKey 为 `kernel.*` 时，表示我们对所有来自 kernel 的日志感兴趣；当我们指定 bindingKey 为 `*.error` 时，表示我们对所有 error 等级的日志感兴趣

在 topic 模式下，我们可以通过使用不同的通配符和单词的组合，使交 topic 类型换机可以实现其他类型交换机的功能，例如指定 bindingKey 为 `#` 时，功能就类似于 fanout 类型交换机；指定 bindingKey 为 `kernel.error` 时，即明确给出了完整的 key，没有使用通配符，这时功能就类似于 direct 类型的交换机\
![主题模式](https://www.rabbitmq.com/img/tutorials/python-five.png)\

### RabbitMQ 整合 Spring

#### 引入依赖
```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
</dependency>
```

#### 在配置文件中声明队列、交换机及绑定关系

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/rabbit
       http://www.springframework.org/schema/rabbit/spring-rabbit.xsd">

    <context:annotation-config />
    <context:property-placeholder location="classpath*:/**/rabbitmq.properties" />

    <!-- 创建 connectionFactory -->
    <rabbit:connection-factory id="connectionFactory"
                               host="${rabbitmq.host}"
                               port="${rabbitmq.port}"
                               virtual-host="${rabbitmq.virtualHost}"
                               username="${rabbitmq.username}"
                               password="${rabbitmq.password}" />

    <!-- 创建一个 rabbit admin 对象来管理交换机、队列，以及两者间的绑定关系 -->
    <rabbit:admin connection-factory="connectionFactory" />

    <!-- 简单模式相关bean -->
    <rabbit:queue id="spring-simple-mode-queue" name="spring-simple-mode-queue" auto-declare="true" />

    <!-- 工作队列模式相关 bean -->
    <rabbit:queue id="spring-work-queue-mode-queue" name="spring-work-queue-mode-queue" auto-declare="true" />

    <!-- 广播模式相关 bean -->
    <!-- 声明队列 -->
    <rabbit:queue id="spring-fanout-mode-queue1" name="spring-fanout-mode-queue1" />
    <rabbit:queue id="spring-fanout-mode-queue2" name="spring-fanout-mode-queue2" />
    <!-- 声明交换机和绑定关系 -->
    <rabbit:fanout-exchange id="spring-fanout-mode-exchange"
                            name="spring-fanout-mode-exchange"
                            auto-declare="true">
        <rabbit:bindings>
            <rabbit:binding queue="spring-fanout-mode-queue1" />
            <rabbit:binding queue="spring-fanout-mode-queue2" />
        </rabbit:bindings>
    </rabbit:fanout-exchange>

    <!-- 路由模式相关 bean -->
    <!-- 声明队列 -->
    <rabbit:queue id="spring-direct-mode-info-queue" name="spring-direct-mode-info-queue" />
    <rabbit:queue id="spring-direct-mode-error-queue" name="spring-direct-mode-error-queue" />
    <!-- 声明交换机和绑定关系 -->
    <rabbit:direct-exchange id="spring-direct-mode-exchange"
                            name="spring-direct-mode-exchange">
        <rabbit:bindings>
            <rabbit:binding queue="spring-direct-mode-info-queue"
                            key="info" />
            <rabbit:binding queue="spring-direct-mode-error-queue"
                            key="error" />
        </rabbit:bindings>
    </rabbit:direct-exchange>
    
    <!-- 主题模式相关 bean -->
    <!-- 声明队列 -->
    <rabbit:queue id="spring-topic-mode-kernel-queue" name="spring-topic-mode-kernel-queue" />
    <rabbit:queue id="spring-topic-mode-error-queue" name="spring-topic-mode-error-queue" />
    <rabbit:queue id="spring-topic-mode-all-queue" name="spring-topic-mode-all-queue" />
    <!-- 声明交换机和绑定关系 -->
    <rabbit:topic-exchange id="spring-topic-mode-exchange"
                           name="spring-topic-mode-exchange">
        <rabbit:bindings>
            <rabbit:binding queue="spring-topic-mode-kernel-queue"
                            pattern="kernel.*" />
            <rabbit:binding queue="spring-topic-mode-error-queue"
                            pattern="*.error" />
            <rabbit:binding queue="spring-topic-mode-all-queue"
                            pattern="#" />
        </rabbit:bindings>
    </rabbit:topic-exchange>

    <!-- 定义 rabbitTemplate 对象来收发消息，这个模板对象可以自动管理与 rabbitmq 服务的连接 -->
    <rabbit:template id="rabbitTemplate" connection-factory="connectionFactory" />

</beans>
```

#### 在 consumer 服务中编写 listener

基础 listener
```java
package com.seamew.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

@Slf4j(topic = "r.BasicMessageListener")
public class BasicMessageListener implements MessageListener
{
    @Override
    public void onMessage(Message message)
    {
        MessageProperties messageProperties = message.getMessageProperties();
        String routingKey = messageProperties.getReceivedRoutingKey();
        String queueName = messageProperties.getConsumerQueue();
        String body = new String(message.getBody());
        log.debug("Message: [{}], routing key: [{}], queue name: [{}]", body, routingKey, queueName);
    }
}
```
简单模式消息 listener
```java
package com.seamew.consumer.listener;

import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleModeMessageListener extends BasicMessageListener implements MessageListener
{

}
```
工作队列模式 listener
```java
package com.seamew.consumer.listener;

import org.springframework.amqp.core.MessageListener;

public class WorkQueueModeMessageListener extends BasicMessageListener implements MessageListener
{

}
```
广播模式 listener
```java
package com.seamew.consumer.listener;

import org.springframework.amqp.core.MessageListener;

public class FanoutModeMessageListener extends BasicMessageListener implements MessageListener
{

}

```
路由模式 listener
```java
package com.seamew.consumer.listener;

import org.springframework.amqp.core.MessageListener;

public class DirectModeMessageListener extends BasicMessageListener implements MessageListener
{

}
```
主题模式 listener
```java
package com.seamew.consumer.listener;

import org.springframework.amqp.core.MessageListener;

public class TopicModeMessageListener extends BasicMessageListener implements MessageListener
{

}

```

#### 在 consumer 服务中编写配置文件将 listener 放入容器中

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/rabbit
       http://www.springframework.org/schema/rabbit/spring-rabbit.xsd">

    <import resource="classpath*:/**/spring-rabbitmq.xml" />
    <context:annotation-config />
    <context:property-placeholder location="classpath*:/**/rabbitmq.properties" />

    <!-- 声明消息监听器 -->
    <!-- 简单模式消息监听器 -->
    <bean id="simpleModeMessageListener" class="com.seamew.consumer.listener.SimpleModeMessageListener" />
    <!-- 工作队列模式消息监听器 -->
    <bean id="workQueueModeMessageListener1" class="com.seamew.consumer.listener.WorkQueueModeMessageListener" />
    <bean id="workQueueModeMessageListener2" class="com.seamew.consumer.listener.WorkQueueModeMessageListener" />
    <!-- 广播模式消息监听器 -->
    <bean id="fanoutModeMessageListener" class="com.seamew.consumer.listener.SimpleModeMessageListener" />
    <!-- 路由模式消息监听器 -->
    <bean id="directModeMessageListener" class="com.seamew.consumer.listener.DirectModeMessageListener" />
    <!-- 主题模式消息监听器 -->
    <bean id="topicModeMessageListener" class="com.seamew.consumer.listener.TopicModeMessageListener" />

    <!-- 指定消息监听器监听的队列 -->
    <rabbit:listener-container connection-factory="connectionFactory">
        <!-- 简单模式 -->
        <rabbit:listener ref="simpleModeMessageListener" queues="spring-simple-mode-queue" />
        <!-- 工作队列模式 -->
        <rabbit:listener ref="workQueueModeMessageListener1" queues="spring-work-queue-mode-queue" />
        <rabbit:listener ref="workQueueModeMessageListener2" queues="spring-work-queue-mode-queue" />
        <!-- 广播模式 -->
        <rabbit:listener ref="fanoutModeMessageListener" queues="spring-fanout-mode-queue1, spring-fanout-mode-queue2" />
        <!-- 路由模式 -->
        <rabbit:listener ref="directModeMessageListener" queues="spring-direct-mode-info-queue, spring-direct-mode-error-queue" />
        <!--主题模式 -->
        <rabbit:listener ref="topicModeMessageListener" queues="spring-topic-mode-all-queue, spring-topic-mode-error-queue, spring-topic-mode-kernel-queue" />
    </rabbit:listener-container>

</beans>
```

#### 编写测试类

producer 服务使用 `RabbitTemplate` 发送消息
```java
package com.seamew.producer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// 使用 spring 的 extension 运行测试用例，这样就可以在测试用例中使用 spring 容器相关功能
// junit5 的 @ExtendWith(SpringExtension.class) 替换了 junit4 的 @RunWith(SpringRunner.class) 注解
@ExtendWith(SpringExtension.class)
// 指明 spring 配置文件位置
@ContextConfiguration("classpath:spring-rabbitmq-producer.xml")
@Slf4j(topic = "r.MessageProducerTest")
public class MessageProducerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;
  
    @Test
    public void testSimpleMessagePublish()
    {
        // 简单模式测试
        // 简单模式下消息会被发送到默认交换机，并以队列名称作为路由键将消息转发到相应的队列中
        String message = "Simple mode";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("", "spring-simple-mode-queue", message);
        log.debug("Complete");
    }
}
```

consumer 服务使用死循环让程序一直运行，以便等待消息
```java
package com.seamew.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConsumerApplication.class)
@Slf4j(topic = "r.MessageConsumerTest")
public class MessageConsumerTest
{
    @Test
    public void bootstrap()
    {
        log.debug("Waiting for messages ...");
        while (true) {

        }
    }
}
```

### RabbitMQ 整合 Springboot

#### 声明队列、交换机及绑定关系

编写 @Configuration 类声明队列、交换机及绑定关系
```java
package com.seamew.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig
{
    // 简单模式相关 bean
    @Bean("springboot-simple-queue")
    public Queue simpleModeQueue()
    {
        return new Queue("springboot-simple-queue");
    }

    // 广播模式相关 bean
    @Bean("springboot-fanout-queue1")
    public Queue fanoutModeQueue1()
    {
        return new Queue("springboot-fanout-queue1");
    }

    @Bean("springboot-fanout-queue2")
    public Queue fanoutModeQueue2()
    {
        return new Queue("springboot-fanout-queue2");
    }

    @Bean("springboot-fanout-exchange")
    public Exchange fanoutModeExchange()
    {
        return ExchangeBuilder
                .fanoutExchange("springboot-fanout-exchange")
                .build();
    }

    @Bean("springboot-fanout-binding1")
    public Binding fanoutModeBinding1(@Qualifier("springboot-fanout-queue1") Queue fanoutModeQueue1,
                                     @Qualifier("springboot-fanout-exchange") Exchange fanoutExchange)
    {
        return BindingBuilder
                .bind(fanoutModeQueue1)
                .to(fanoutExchange)
                .with("")
                .noargs();
    }

    @Bean("springboot-fanout-binding2")
    public Binding fanoutModeBinding2(@Qualifier("springboot-fanout-queue2") Queue fanoutModeQueue2,
                                     @Qualifier("springboot-fanout-exchange") Exchange fanoutExchange)
    {
        return BindingBuilder
                .bind(fanoutModeQueue2)
                .to(fanoutExchange)
                .with("")
                .noargs();
    }
}
```

#### 创建 listener
在 consumer 服务中创建 listener，直接在方法上加上 `@RabbitListener` 即可
```java
package com.seamew.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "r.RabbitMessageListener")
public class RabbitMessageListener
{
    @RabbitListener(queues = "springboot-simple-queue")
    public void onSimpleModeMessage(Message message)
    {
        MessageProperties messageProperties = message.getMessageProperties();
        String routingKey = messageProperties.getReceivedRoutingKey();
        String queueName = messageProperties.getConsumerQueue();
        String body = new String(message.getBody());
        log.debug("Message: [{}], routing key: [{}], queue name: [{}]", body, routingKey, queueName);
    }

    @RabbitListener(queues = { "springboot-fanout-queue1", "springboot-fanout-queue2" })
    public void onFanoutModeMessage(Message message)
    {
        MessageProperties messageProperties = message.getMessageProperties();
        String routingKey = messageProperties.getReceivedRoutingKey();
        String queueName = messageProperties.getConsumerQueue();
        String body = new String(message.getBody());
        log.debug("Message: [{}], routing key: [{}], queue name: [{}]", body, routingKey, queueName);
    }
}
```

#### 编写配置文件

在 producer 服务中编写配置文件
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 15672
    virtual-host: /
    username: seamew
    password: ltr20001121
```

#### 测试

在 consumer 服务中加上启动类后，编写测试类

```java
package com.seamew.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConsumerApplication.class)
@Slf4j(topic = "r.MessageConsumerTest")
public class MessageConsumerTest
{
    @Test
    public void bootstrap()
    {
        log.debug("Waiting for messages ...");
        while (true) {

        }
    }
}
```

在 producer 服务中加上启动类后，编写测试类

```java
package com.seamew.producer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProducerApplication.class)
@Slf4j(topic = "r.MessageProducer")
public class MessageProducerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleMessagePublish()
    {
        // 简单模式测试
        // 简单模式下消息会被发送到默认交换机，并以队列名称作为路由键将消息转发到相应的队列中
        String message = "Simple mode";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("", "springboot-simple-queue", message);
        log.debug("Complete");
    }

    @Test
    public void testFanoutLogPublish()
    {
        // 发布/订阅模式测试
        String message = "Fanout mode log";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("springboot-fanout-exchange", "", message);
        log.debug("Complete");
    }
}
```

### RabbitMQ 高级特性

#### 消息可靠投递

##### Confirm 模式
> 在 Confirm 模式开启的情况下，当生产者给 broker 发消息后，如果消息成功到达了交换机，那么生产者会收到一个确认消息，如果消息未能发送到交换机，生产者也会收到错误消息。这种机制能够确保消息准确的发送到交换机

配置方式 - 以 Springboot 为例

在 `application.yml` 中设置 `ConnectionFactory` 中的 `publisherConfirmType` 为 `correlated`
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    virtual-host: /
    username: seamew
    password: ltr20001121
    publisher-confirm-type: correlated
```

Confirm 模式回调函数
```java
package com.seamew.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j(topic = "r.ConfirmCallback")
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback
{
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause)
    {
        if (ack) {
            log.debug("Message received");
        } else {
            log.debug("Fail to receive message. Cause: {}", cause);
        }
    }
}
```

在 `RabbitTemplate` 中设置 Confirm 模式的回调函数
```java
rabbitTemlate.setConfirmCallback(new ConfirmCallback());
```

##### Return 模式

> 在 Return 模式开启的情况下，当生产者给 broker 发消息并且交换机成功接收到消息后，如果未能成功将消息发送到队列，那么 broker 会向生产者 "退回" 该消息，以告知生产者这条消息发送失败了

Return 模式配置方式 - 以 Springboot 为例

在 `application.yml` 中设置 `ConnectionFactory` 的 `publisherReturns` 属性为 `true`，并设置 `RabbitTemplate` 的 `mandatory` 属性为 `true`
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    virtual-host: /
    username: seamew
    password: ltr20001121
    publisher-returns: true
    template:
      mandatory: true
```

Return 模式回调函数
```java
package com.seamew.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j(topic = "r.ReturnCallback")
public class ReturnCallback implements RabbitTemplate.ReturnsCallback
{
    @Override
    public void returnedMessage(ReturnedMessage returned)
    {
        int replyCode = returned.getReplyCode();
        String replyText = returned.getReplyText();
        Message message = returned.getMessage();
        String exchange = returned.getExchange();
        String routingKey = returned.getRoutingKey();
        log.debug("ReplyCode: [{}]", replyCode);
        log.debug("ReplyText: [{}]", replyText);
        log.debug("Message: [{}]", message);
        log.debug("Exchange: [{}]", exchange);
        log.debug("RoutingKey: [{}]", routingKey);
    }
}
```

在 `RabbitTemplate` 中设置 Return 模式的回调函数
```java
rabbitTemplate.setReturnsCallback(new ReturnCallback());
```

##### 消费者手动 ACK
> 消费者成功从队列中接收到消息后，可能会执行一些业务逻辑处理，如果整个过程没有发生错误，消费者可以手动确认接收消息，如果发生错误，消费者可以拒收消息，并可以选择是否要将消息重新放入队列中。这种方式保证了当消费者接收到消息执行业务逻辑出错后，消息不会丢失

配置方式 - 以 Springboot 为例

在 `application.yml` 中配置 `ListenerContainer` 的 `acknowledgeMode` 属性为 `manual`
```yaml
server:
  port: 9010

spring:
  rabbitmq:
    host: localhost
    port: 5672
    virtual-host: /
    username: seamew
    password: ltr20001121
    listener:
      simple:
        acknowledge-mode: manual
      direct:
        acknowledge-mode: manual
```

使用 `@RabbitListener` 方法监听消息
```java
@RabbitListener(queues = "ack-queue")
public void onMessageDoManualAck(Message message, Channel channel) throws IOException
{
    MessageProperties messageProperties = message.getMessageProperties();
    long deliveryTag = messageProperties.getDeliveryTag();
    try {
        // Some business logic ...
        log.debug("Do some business logic ...");
        // 设置 50% 的概率发生异常
        double random = Math.random();
        if (random <= 0.5) {
            // 如果业务逻辑未发生异常，就签收消息
            channel.basicAck(deliveryTag, false);
            log.debug("basicAck() performed");
        } else {
            int i = 1 / 0;
        }
    } catch (Exception e) {
        log.debug("Error occurred! Performing basicNack()", e);
        // 如果业务逻辑发生异常，拒签消息，并将消息重新放入队列中，这里要注意，由于监听器还在监听该队列，
        // 一旦消息放回队列，onMessageDoManualAck 方法有回被重新执行，一直循环往复，直至方法不抛出异常
        channel.basicNack(deliveryTag, false, true);
        log.debug("basicNack() performed");
    }
}
```

##### 消息可靠性投递总结
- 持久化
  - Exchange 持久化
  - Queue 持久化
  - Message 持久化
- 生产者启用 broker 的消息确认 (Confirm) 和退回 (Return) 模式
- 消费者收到消息后手动确认接收消息 (Ack)
- Broker 高可用