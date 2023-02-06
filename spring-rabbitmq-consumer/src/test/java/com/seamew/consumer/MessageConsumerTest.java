package com.seamew.consumer;

import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-rabbitmq.xml")
@Slf4j(topic = "r.MessageConsumerTest")
public class MessageConsumerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.simpleMode.queueName}")
    public String simpleModeQueueName;

    @Value("${rabbitmq.queue.workQueueMode.queueName}")
    public String workQueueModeQueueName;

    @Test
    public void testSimpleMessageReceive()
    {
        // 测试简单模式
        Object message = rabbitTemplate.receiveAndConvert(simpleModeQueueName);
        log.debug("Waiting for messages ...");
        log.debug("Message: [{}]", message);
    }

    @Test
    public void testTaskReceive()
    {
        // 测试工作队列模式
    }

    @Test
    public void testFanoutLogReceive()
    {
        // 测试发布/订阅模式
    }

    @Test
    public void testDirectLogReceive()
    {
        // 测试路由模式
    }

    @Test
    public void testTopicLogReceive()
    {
        // 测试主题模式
    }
}
