package com.seamew.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j(topic = "r.ReturnCallback")
public class ReturnCallback implements RabbitTemplate.ReturnsCallback
{
    @Override
    public void returnedMessage(ReturnedMessage returned)
    {
        int replyCode = returned.getReplyCode();
        String replyText = returned.getReplyText();
        Message message = returned.getMessage();
        String exchange = returned.getExchange();
        String routingKey = returned.getRoutingKey();
        log.debug("ReplyCode: [{}]", replyCode);
        log.debug("ReplyText: [{}]", replyText);
        log.debug("Message: [{}]", message);
        log.debug("Exchange: [{}]", exchange);
        log.debug("RoutingKey: [{}]", routingKey);
    }
}
