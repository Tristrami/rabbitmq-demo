package com.seamew.consumer.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class FanoutModeMessageListener extends BasicMessageListener implements MessageListener
{

}
