package com.seamew.consumer.listener;

import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleModeMessageListener extends BasicMessageListener implements MessageListener
{

}
