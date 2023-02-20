package com.seamew.producer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// 使用 spring 的 extension 运行测试用例，这样就可以在测试用例中使用 spring 容器相关功能
// junit5 的 @ExtendWith(SpringExtension.class) 替换了 junit4 的 @RunWith(SpringRunner.class) 注解
@ExtendWith(SpringExtension.class)
// 指明 spring 配置文件位置
@ContextConfiguration("classpath:spring-rabbitmq-producer.xml")
@Slf4j(topic = "r.MessageProducerTest")
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
        rabbitTemplate.convertAndSend("", "spring-simple-mode-queue", message);
        log.debug("Complete");
    }

    @Test
    public void testTaskPublish()
    {
        // 工作队列模式测试
        String basicMessage = "WorkQueue mode";
        for (int i = 0; i < 10; i++) {
            String message = basicMessage + i;
            log.debug("Sending message [{}]", message);
            rabbitTemplate.convertAndSend("", "spring-work-queue-mode-queue", message);
        }
        log.debug("Complete");
    }

    @Test
    public void testFanoutLogPublish()
    {
        // 发布/订阅模式测试
        String message = "Fanout mode log";
        log.debug("Sending message [{}]", message);
        rabbitTemplate.convertAndSend("spring-fanout-mode-exchange", "", message);
        log.debug("Complete");
    }

    @ParameterizedTest
    @ValueSource(strings = {"info", "error"})
    public void testDirectLogPublish(String routingKey)
    {
        // 路由模式测试
        String message = "Direct mode " + routingKey + " log";
        log.debug("Sending message [{}], routingKey: [{}]", message, routingKey);
        rabbitTemplate.convertAndSend("spring-direct-mode-exchange", routingKey, message);
        log.debug("Complete");
    }

    @ParameterizedTest
    @ValueSource(strings = {"kernel.info", "kernel.error", "cron.error"})
    public void testTopicLogPublish(String routingKey)
    {
        // 主题模式测试
        String message = "Topic mode " + routingKey + " log";
        log.debug("Sending message [{}], routingKey: [{}]", message, routingKey);
        rabbitTemplate.convertAndSend("spring-topic-mode-exchange", routingKey, message);
        log.debug("Complete");
    }
}
