package com.seamew.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// 使用 spring 的 extension 运行测试用例，这样就可以在测试用例中使用 spring 容器相关功能
// junit5 的 @ExtendWith(SpringExtension.class) 替换了 junit4 的 @RunWith(SpringRunner.class) 注解
@ExtendWith(SpringExtension.class)
// 指明 spring 配置文件位置
@ContextConfiguration("classpath*:/**/spring-rabbitmq.xml")
@Slf4j(topic = "r.MessageConsumerTest")
public class MessageConsumerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.simpleMode.queueName}")
    public String simpleModeQueueName;

    @Value("${rabbitmq.queue.workQueueMode.queueName}")
    public String workQueueModeQueueName;

    @BeforeEach
    public void init()
    {
        // 设置超时时间小于 0 时为无限等待，rabbitTemplate 的默认超时时间是 0，
        // 当没有消息时会直接返回 null，不会阻塞。当收到消息后也会立即返回消息，
        // 同样不会阻塞
        rabbitTemplate.setReceiveTimeout(-1);
    }

    @Test
    public void testSimpleMessageReceive()
    {
        // 测试简单模式
        log.debug("Waiting for messages ...");
        Object message = rabbitTemplate.receiveAndConvert(simpleModeQueueName);
        log.debug("Message: [{}]", message);
    }

    @Test
    public void testTaskReceive()
    {
        // 测试工作队列模式，需启动多个 receiver 实例
        log.debug("Waiting for messages ...");
        while (true) {
            // rabbitTemplate 收到消息后会立即返回，不会阻塞，所以用死循环保证可以一直等待消息
            Object message = rabbitTemplate.receiveAndConvert(workQueueModeQueueName);
            log.debug("Message: [{}]", message);
        }
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
