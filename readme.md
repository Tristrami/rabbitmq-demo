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