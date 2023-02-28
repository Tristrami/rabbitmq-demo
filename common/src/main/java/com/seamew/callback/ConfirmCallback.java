package com.seamew.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j(topic = "r.ConfirmCallback")
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback
{
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause)
    {
        if (ack) {
            log.debug("Message received");
        } else {
            log.debug("Fail to receive message");
        }
    }
}
