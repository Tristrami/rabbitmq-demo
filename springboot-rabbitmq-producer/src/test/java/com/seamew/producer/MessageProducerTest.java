package com.seamew.producer;

import com.seamew.producer.callback.ConfirmCallback;
import com.seamew.producer.callback.ReturnCallback;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProducerApplication.class)
@Slf4j(topic = "r.MessageProducer")
public class MessageProducerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void init()
    {
        // 设置 confirm 模式回调函数
        rabbitTemplate.setConfirmCallback(new ConfirmCallback());
        // 设置 return 模式回调函数
        rabbitTemplate.setReturnsCallback(new ReturnCallback());
    }

    @Test
    public void testSimpleMessagePublish()
    {
        // 简单模式测试
        // 简单模式下消息会被发送到默认交换机，并以队列名称作为路由键将消息转发到相应的队列中
        String message = "Simple mode";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("", "springboot-simple-queue", message);
        log.debug("Complete");
    }

    @Test
    public void testFanoutLogPublish()
    {
        // 发布/订阅模式测试
        String message = "Fanout mode log";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("springboot-fanout-exchange", "", message);
        log.debug("Complete");
    }

    @Test
    public void testConfirmCallback()
    {
        String message = "Confirm callback test";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("confirm-exchange", "confirm", message);
    }
}
