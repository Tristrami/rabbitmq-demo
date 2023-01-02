package com.seamew.simple;

import com.rabbitmq.client.*;
import com.seamew.common.ConsumerCallback;
import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SimpleConsumer
{
    // 简单模式，一个生产者生产消息，一个队列存消息，一个消费者从队列中获取消息
    public static void main(String[] args)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("seamew");
        connectionFactory.setPassword("ltr20001121");
        try {
            // 创建 connection 和 channel
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            // 这里需要从队列中获取消息，为了防止队列不存在，先声明队列
            channel.queueDeclare(QueueConfig.SIMPLE_MODE_QUEUE_NAME, true, false, false, null);
            // 监听 (非阻塞) 队列, 自动从队列中接受消息, ConsumerCallback 是获取到消息后的回调函数，决定如何处理获取到的消息
            log.info("Waiting for message from queue [{}] ...", QueueConfig.SIMPLE_MODE_QUEUE_NAME);
            channel.basicConsume(QueueConfig.SIMPLE_MODE_QUEUE_NAME, true, new ConsumerCallback(channel));
        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }
}
