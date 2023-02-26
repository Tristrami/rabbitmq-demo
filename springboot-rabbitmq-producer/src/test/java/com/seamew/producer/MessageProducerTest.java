package com.seamew.producer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProducerApplication.class)
@Slf4j(topic = "r.MessageProducer")
public class MessageProducerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("rabbitTemplateWithCallback")
    private RabbitTemplate rabbitTemplateWithCallback;

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

    @ParameterizedTest
    @ValueSource(strings = { "confirm-exchange", "non-existent-exchange" })
    public void testConfirmCallback(String exchangeName) throws InterruptedException
    {
        // 消息确认机制能保证消息准确发送到交换机
        String message = "Confirm callback test";
        log.debug("Sending message [{}]", message);
        rabbitTemplateWithCallback.convertAndSend(exchangeName, "confirm", message);
        // rabbitTemplate 执行完操作后会立刻逻辑关闭 channel，这里等待 10 s，保证 confirmCallback 能够返回正确的结果
        Thread.sleep(1000);
    }

    @ParameterizedTest
    @ValueSource(strings = { "return", "non-existent-routing-key" })
    public void testReturnCallback(String routingKey) throws InterruptedException
    {
        // 当交换机接收到消息后，如果无法将消息发送到队列，消息就会被退回
        String message = "Return callback test";
        log.debug("Sending message [{}]", message);
        rabbitTemplateWithCallback.convertAndSend("return-exchange", routingKey, message);
        Thread.sleep(1000);
    }

    @Test
    public void testConsumerAck()
    {
        String message = "Consumer acknowledgement";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("ack-queue", message);
    }
}
