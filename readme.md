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
开启 rabbitmq 的 web 监控台，rabbitmq 的 web 监控台默认的端口为 `15672`
```shell
$RABBITMQ_HOME/sbin/rabbitmq-plugins enable rabbitmq_management
```  

### RabbitMQ Messaging Model
- A producer is a user application that sends messages.
- An exchange is a very simple thing. On one side it receives messages from producers and the other side it pushes them to queues.The exchange must know exactly what to do with a message it receives. Should it be appended to a particular queue? Should it be appended to many queues? Or should it get discarded. The rules for that are defined by the exchange type.
  - <span id="exchange-type-direct">direct</span>
  - <span id="exchange-type-topic">topic</span>
  - <span id="exchange-type-headers">headers</span>
  - <span id="exchange-type-fanout">fanout</span> : It broadcasts all the messages it receives to all the queues it knows
- A queue is a buffer that stores messages.
- A consumer is a user application that receives messages.

### RabbitMQ 工作模式
#### 简单模式 - Hello World
> The simplest thing that does something\
> https://rabbitmq.com/tutorials/tutorial-one-java.html

**一个** 生产者生产消息，经由交换机发送到 **一个** 队列中，**一个** 消费者从该队列中接收消息\
![简单模式](https://rabbitmq.com/img/tutorials/python-one-overall.png)

#### 工作队列模式 - Work Queue
> Distributing tasks among workers (the competing consumers pattern)\
> https://rabbitmq.com/tutorials/tutorial-two-java.html

**一个** 生产者生产多条消息，消息经由交换机发送到 **一个** 队列中，**多个** 消费者从该队列中争抢获取消息，相当于将这些消息 **分发** 给多个消费者，该模式下， **一条** 消息只会发给 **一个** 消费者\
![工作队列模式](https://rabbitmq.com/img/tutorials/python-two.png)

#### 发布订阅模式 - Publish / Subscribe
> Sending messages to many consumers at once\
> https://rabbitmq.com/tutorials/tutorial-three-java.html

**一个** 生产者生产消息，消息经由 **[fanout](#exchange-type-fanout)** 类型的交换机发送到 **所有** 与该交换机绑定的队列中，**订阅** 了该队列的所有消费者都从队列中获取 **同一条** 消息，该模式下，**同一条** 消息会发给 **多个** 消费者\
![订阅发布模式](https://rabbitmq.com/img/tutorials/python-three-overall.png)