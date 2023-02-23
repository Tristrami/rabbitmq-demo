package com.seamew.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:spring-rabbitmq/rabbitmq.properties")
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

    // Confirm 模式相关 bean
    @Bean("confirm-queue")
    public Queue confirmQueue()
    {
        return new Queue("confirm-queue");
    }

    @Bean("confirm-exchange")
    public Exchange confirmExchange()
    {
        return ExchangeBuilder
                .directExchange("confirm-exchange")
                .build();
    }

    @Bean("confirm-binding")
    public Binding confirmBinding(@Qualifier("confirm-queue") Queue queue,
                                  @Qualifier("confirm-exchange") Exchange exchange)
    {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("confirm")
                .noargs();
    }

    // Return 模式相关 bean
    @Bean("return-queue")
    public Queue returnQueue()
    {
        return new Queue("return-queue");
    }

    @Bean("return-exchange")
    public Exchange returnExchange()
    {
        return ExchangeBuilder
                .directExchange("return-exchange")
                .build();
    }

    @Bean("return-binding")
    public Binding returnBinding(@Qualifier("return-queue") Queue queue,
                                 @Qualifier("return-exchange") Exchange exchange)
    {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("return")
                .noargs();
    }
}
