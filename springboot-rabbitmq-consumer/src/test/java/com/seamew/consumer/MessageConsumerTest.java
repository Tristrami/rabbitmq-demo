package com.seamew.consumer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConsumerApplication.class)
@Slf4j(topic = "r.MessageConsumerTest")
public class MessageConsumerTest
{
    @Test
    public void bootstrap()
    {
        log.debug("Waiting for messages ...");
        while (true) {

        }
    }
}
