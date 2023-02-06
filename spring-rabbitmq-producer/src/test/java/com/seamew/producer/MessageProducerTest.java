package com.seamew.producer;

import com.seamew.config.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 使用 spring 的 runner 运行测试用例，这样就可以在测试用例中使用 spring 容器相关功能
@RunWith(SpringJUnit4ClassRunner.class)
// 指明 spring 配置文件位置
@ContextConfiguration("classpath:spring-rabbitmq.xml")
@Slf4j(topic = "r.MessageProducerTest")
public class MessageProducerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.simpleMode.queueName}")
    public String simpleModeQueueName;

    @Value("${rabbitmq.queue.workQueueMode.queueName}")
    public String workQueueModeQueueName;

    @Test
    public void testSimpleMessagePublish()
    {
        // 简单模式测试
        // 简单模式下消息会被发送到默认交换机，并以队列名称作为路由键将消息转发到相应的队列中
        String msg = "Simple mode";
        log.debug("Sending message [{}]", msg);
        rabbitTemplate.convertAndSend("", simpleModeQueueName, msg);
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
            rabbitTemplate.convertAndSend("", "spring-workqueue-queue", "");
        }
    }

    @Test
    public void testFanoutLogPublish()
    {
        // 发布/订阅模式测试
    }

    @Test
    public void testDirectLogPublish()
    {
        // 路由模式测试
    }

    @Test
    public void testTopicLogPublish()
    {
        // 主题模式测试
    }
}
