package com.seamew.common;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ConsumerCallback extends DefaultConsumer
{

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public ConsumerCallback(Channel channel)
    {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
    {
        // consumer 的唯一标识符
        log.info("ConsumerTag: [{}]", consumerTag);
        // 消息的唯一标识符
        log.info("DeliveryTag: [{}]", envelope.getDeliveryTag());
        // 交换机
        log.info("Exchange: [{}]", envelope.getExchange());
        // 路由 key
        log.info("RoutingKey: [{}]", envelope.getRoutingKey());
        // 消息体
        log.info("Body: [{}]", new String(body));
    }
}
