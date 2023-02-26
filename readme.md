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
![主题模式](https://www.rabbitmq.com/img/tutorials/python-five.png)

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