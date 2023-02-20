package com.seamew.producer;

import com.seamew.config.RabbitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RabbitConfig.class)
public class ProducerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ProducerApplication.class, args);
    }
}
