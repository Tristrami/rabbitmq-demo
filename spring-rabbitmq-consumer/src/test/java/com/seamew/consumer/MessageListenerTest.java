package com.seamew.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:spring-rabbitmq-listener.xml")
public class MessageListenerTest
{
    @Test
    public void bootstrap()
    {
        while (true) {

        }
    }
}
