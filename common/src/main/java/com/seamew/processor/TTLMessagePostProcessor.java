package com.seamew.processor;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

public class TTLMessagePostProcessor implements MessagePostProcessor
{
    private String ttl;

    public TTLMessagePostProcessor(String ttl)
    {
        this.ttl = ttl;
    }

    @Override
    public Message postProcessMessage(Message message) throws AmqpException
    {
        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setExpiration(ttl);
        return message;
    }
}
