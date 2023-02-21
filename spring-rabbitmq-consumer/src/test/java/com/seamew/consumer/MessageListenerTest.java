package com.seamew.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:spring-rabbitmq-listener.xml")
@Slf4j(topic = "r.MessageListenerTest")
public class MessageListenerTest
{
    @Test
    public void bootstrap()
    {
        // 启动 spring 容器，使用死循环保证程序不会终止，以便观察 MessageListener
        log.debug("Waiting for messages ...");
        while (true) {

        }
    }
}
