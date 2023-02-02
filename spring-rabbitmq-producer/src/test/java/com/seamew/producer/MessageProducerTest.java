package com.seamew.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 使用 spring 的 runner 运行测试用例，这样就可以在测试用例中使用 spring 容器相关功能
@RunWith(SpringJUnit4ClassRunner.class)
// 指明 spring 配置文件位置
@ContextConfiguration("classpath:spring-rabbitmq.xml")
public class MessageProducerTest
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleMessagePublish()
    {
        // 简单模式测试

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
