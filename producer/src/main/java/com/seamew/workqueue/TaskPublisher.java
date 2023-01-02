package com.seamew.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class TaskPublisher
{
    public static void main(String[] args)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("seamew");
        connectionFactory.setPassword("ltr20001121");
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QueueConfig.WORK_QUEUE_MODE_QUEUE_NAME, true, false, false, null);
            // 连续发送十条消息，模拟发布十个任务
            for (int i = 0; i < 10; i++) {
                String message = "task " + i;
                log.info("Sending message [{}] default exchange ...", message);
                channel.basicPublish("", QueueConfig.WORK_QUEUE_MODE_QUEUE_NAME, null, message.getBytes());
                log.info("Message sent successfully!");
            }
        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }
}
