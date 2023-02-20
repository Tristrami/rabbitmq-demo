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
        log.debug("Waiting for messages ...");
        while (true) {

        }
    }
}
