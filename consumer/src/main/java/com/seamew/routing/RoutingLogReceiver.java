package com.seamew.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.seamew.callback.ConsumerCallback;
import com.seamew.config.ExchangeConfig;
import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RoutingLogReceiver
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
            String[] names = QueueConfig.ROUTING_MODE_QUEUE_NAMES;
            String[] keys = QueueConfig.ROUTING_MODE_KEYS;
            // 为防止 RoutingLogReceiver 先启动时交换机不存在，先声明交换机
            channel.exchangeDeclare(ExchangeConfig.ROUTING_MODE_EXCHANGE_NAME, "direct");
            // 为每种日志级别用级别名称声明队列，并绑定到 direct 类型到交换机上，指定 routingKey 为日志级别名称
            for (int i = 0; i < names.length; i++) {
                channel.queueDeclare(names[i], true, false, false, null);
                channel.queueBind(names[i], ExchangeConfig.ROUTING_MODE_EXCHANGE_NAME, keys[i]);
            }
            // 接收消息
            log.info("Waiting for messages ...");
            for (String name : QueueConfig.ROUTING_MODE_QUEUE_NAMES) {
                channel.basicConsume(name, new ConsumerCallback(channel));
            }
        } catch (TimeoutException | IOException e) {
            log.error("", e);
        }
    }
}
