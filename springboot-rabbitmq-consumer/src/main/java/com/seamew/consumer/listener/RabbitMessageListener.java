package com.seamew.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
}
