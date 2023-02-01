package com.seamew.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.callback.ConsumerCallback;
import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Worker
{
    // 工作队列模式，启动多个 Worker 实例程序，从同一个队列中同时争抢获取消息，每个消息代表一个任务，
    // 以此来模拟多个 worker 从同一个队列中争抢任务，注意要在运行选项里先启动允许多个实例运行
    // Distributing tasks among workers (the competing consumers pattern)
    // 测试方法: 先启动两个 Worker 实例程序，再启动 TaskPublisher
    public static void main(String[] args)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("seamew");
        connectionFactory.setPassword("ltr20001121");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QueueConfig.WORK_QUEUE_MODE_QUEUE_NAME, true, false, false, null);
            log.info("Waiting for message from queue [{}] ...", QueueConfig.WORK_QUEUE_MODE_QUEUE_NAME);
            channel.basicConsume(QueueConfig.WORK_QUEUE_MODE_QUEUE_NAME, true, new ConsumerCallback(channel));
        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }
}
