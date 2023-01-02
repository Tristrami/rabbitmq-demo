package com.seamew.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SimpleProducer
{
    public static void main(String[] args)
    {
        // 构造 connectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("seamew");
        connectionFactory.setPassword("ltr20001121");
        // 创建 connection，创建 channel
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            String message = "simple mode";
            log.info("Sending message [{}] to default exchange ...", message);
            // 要向队列中发消息，先声明队列
            channel.queueDeclare(QueueConfig.SIMPLE_MODE_QUEUE_NAME, true, false, false, null);
            // 发送消息
            channel.basicPublish("", QueueConfig.SIMPLE_MODE_QUEUE_NAME, null, message.getBytes());
            log.info("Message sent successfully!");
        } catch (IOException | TimeoutException e) {
            log.error("Error occurred while sending message:\n", e);
        }
    }
}
