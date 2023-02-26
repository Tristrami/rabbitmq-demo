package com.seamew.consumer.listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j(topic = "r.RabbitMessageListener")
public class RabbitMessageListener
{
    @RabbitListener(queues = "springboot-simple-queue")
    public void onSimpleModeMessage(Message message)
    {
        MessageProperties messageProperties = message.getMessageProperties();
        String routingKey = messageProperties.getReceivedRoutingKey();
        String queueName = messageProperties.getConsumerQueue();
        String body = new String(message.getBody());
        log.debug("Message: [{}], routing key: [{}], queue name: [{}]", body, routingKey, queueName);
    }

    @RabbitListener(queues = { "springboot-fanout-queue1", "springboot-fanout-queue2" })
    public void onFanoutModeMessage(Message message)
    {
        MessageProperties messageProperties = message.getMessageProperties();
        String routingKey = messageProperties.getReceivedRoutingKey();
        String queueName = messageProperties.getConsumerQueue();
        String body = new String(message.getBody());
        log.debug("Message: [{}], routing key: [{}], queue name: [{}]", body, routingKey, queueName);
    }

    @RabbitListener(queues = "ack-queue")
    public void onMessageDoManualAck(Message message, Channel channel) throws IOException
    {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            // Some business logic ...
            log.debug("Do some business logic ...");
            // 设置 50% 的概率发生异常
            double random = Math.random();
            if (random <= 0.5) {
                // 如果业务逻辑未发生异常，就签收消息
                channel.basicAck(deliveryTag, false);
                log.debug("basicAck() performed");
            } else {
                int i = 1 / 0;
            }
        } catch (Exception e) {
            log.debug("Error occurred! Performing basicNack()", e);
            // 如果业务逻辑发生异常，拒签消息，并将消息重新放入队列中，这里要注意，由于监听器还在监听该队列，
            // 一旦消息放回队列，onMessageDoManualAck 方法有回被重新执行，一直循环往复，直至方法不抛出异常
            channel.basicNack(deliveryTag, false, true);
            log.debug("basicNack() performed");
        }
    }
}
