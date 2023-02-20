package com.seamew.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

@Slf4j(topic = "r.BasicMessageListener")
public class BasicMessageListener implements MessageListener
{
    @Override
    public void onMessage(Message message)
    {
        MessageProperties messageProperties = message.getMessageProperties();
        String routingKey = messageProperties.getReceivedRoutingKey();
        String queueName = messageProperties.getConsumerQueue();
        String body = new String(message.getBody());
        log.debug("Message: [{}], routing key: [{}], queue name: [{}]", body, routingKey, queueName);
    }
}
