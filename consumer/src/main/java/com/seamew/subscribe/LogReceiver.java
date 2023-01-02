package com.seamew.subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.common.ConsumerCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class LogReceiver
{
    // 发布 / 订阅模式，LogPublisher 发布日志消息，多个 LogReceiver 实例订阅该消息
    // 测试方法: 先启动多个 LogReceiver 实例 (control + option + u 开启允许 idea 启动多个实例)，再启动 LogPublisher
    public static void main(String[] args)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("seamew");
        connectionFactory.setPassword("ltr20001121");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            // 先启动 LogReceiver 时，可能交换机不存在，所以先声明
            channel.exchangeDeclare("logging-exchange", "fanout");
            // 创建一个随机名字的队列，这样做的原因是，我们想要每一个 LogReceiver 实例都订阅 LogPublisher 发送的消息，
            // 而 LogPublisher 将消息发送到了 fanout 类型的一个交换机，对于每一个 LogReceiver 实例来说，如果想要获取
            // 这个消息，就需要先声明一个队列和这个交换机绑定，然后 LogReceiver 就可以从这个队列中获取到消息，所以
            // LogReceiver 实例 "订阅" 这一动作实际上分为两步 -- 声明一个队列并将其绑定到存放消息的交换机 -- LogReceiver
            // 实例从队列中读取消息，也就是说，对于每一个 LogReceiver 实例而言，声明的队列仅供每个实例自己使用，实例销毁后
            // 队列随之销毁，并不会被复用，所以我们并不需要关心队列的名字
            // 使用 channel.queueDeclare() 声明的队列名字随机，会自动删除，不进行消息持久化，并且互斥
            String randomQueueName = channel.queueDeclare().getQueue();
            // 将这个队列和 fanout 交换机绑定
            channel.queueBind(randomQueueName, "logging-exchange", "");
            log.info("Waiting for message from queue [{}] ...", randomQueueName);
            // 从队列中获取消息
            channel.basicConsume(randomQueueName, new ConsumerCallback(channel));
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
