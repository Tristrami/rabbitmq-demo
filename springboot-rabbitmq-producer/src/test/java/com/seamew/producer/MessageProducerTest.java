package com.seamew.producer;

import lombok.extern.slf4j.Slf4j;
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
}
