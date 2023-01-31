package com.seamew.publish;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.config.ExchangeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

@Slf4j
public class FanoutLogPublisher
{
    public static void main(String[] args)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 端口号默认为 5672，virtualHost 默认为 /
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("seamew");
        connectionFactory.setPassword("ltr20001121");
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明 fanout 类型的交换机，该类型的交换机会向订阅了该交换机的队列广播日志信息
            channel.exchangeDeclare(ExchangeConfig.SUBSCRIBE_PUBLISH_MODE_EXCHANGE_NAME, "fanout");
            String message = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    " [LogPublisher] - " + "This is a logging message";
            log.info("Sending message [{}] to exchange [{}] ...", message, ExchangeConfig.SUBSCRIBE_PUBLISH_MODE_EXCHANGE_NAME);
            // 将消息发送到交换机
            channel.basicPublish(ExchangeConfig.SUBSCRIBE_PUBLISH_MODE_EXCHANGE_NAME, "", null, message.getBytes());
            log.info("Message sent successfully!");
        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }
}
