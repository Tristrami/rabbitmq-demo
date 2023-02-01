package com.seamew.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.callback.ConsumerCallback;
import com.seamew.config.ExchangeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class TopicLogReceiver
{
    public static void main(String[] args)
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("seamew");
        factory.setPassword("ltr20001121");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(ExchangeConfig.TOPIC_MODE_EXCHANGE_NAME, "topic");
            String queueName1 = channel.queueDeclare().getQueue();
            String queueName2 = channel.queueDeclare().getQueue();
            // queue1 接收所有来自 kernel 的日志
            channel.queueBind(queueName1, ExchangeConfig.TOPIC_MODE_EXCHANGE_NAME, "kernel.*");
            // queue2 接受所有 error 等级的日志
            channel.queueBind(queueName2, ExchangeConfig.TOPIC_MODE_EXCHANGE_NAME, "*.error");
            // 接受消息
            log.info("Waiting for messages ...");
            channel.basicConsume(queueName1, new ConsumerCallback(channel));
            channel.basicConsume(queueName2, new ConsumerCallback(channel));
        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }
}
