package com.seamew.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig
{
    // 简单模式相关 bean
    @Bean("springboot-simple-queue")
    public Queue simpleModeQueue()
    {
        return new Queue("springboot-simple-queue");
    }

    // 广播模式相关 bean
    @Bean("springboot-fanout-queue1")
    public Queue fanoutModeQueue1()
    {
        return new Queue("springboot-fanout-queue1");
    }

    @Bean("springboot-fanout-queue2")
    public Queue fanoutModeQueue2()
    {
        return new Queue("springboot-fanout-queue2");
    }

    @Bean("springboot-fanout-exchange")
    public Exchange fanoutModeExchange()
    {
        return ExchangeBuilder
                .fanoutExchange("springboot-fanout-exchange")
                .build();
    }

    @Bean("springboot-fanout-binding1")
    public Binding fanoutModeBinding1(@Qualifier("springboot-fanout-queue1") Queue fanoutModeQueue1,
                                     @Qualifier("springboot-fanout-exchange") Exchange fanoutExchange)
    {
        return BindingBuilder
                .bind(fanoutModeQueue1)
                .to(fanoutExchange)
                .with("")
                .noargs();
    }

    @Bean("springboot-fanout-binding2")
    public Binding fanoutModeBinding2(@Qualifier("springboot-fanout-queue2") Queue fanoutModeQueue2,
                                     @Qualifier("springboot-fanout-exchange") Exchange fanoutExchange)
    {
        return BindingBuilder
                .bind(fanoutModeQueue2)
                .to(fanoutExchange)
                .with("")
                .noargs();
    }
}
