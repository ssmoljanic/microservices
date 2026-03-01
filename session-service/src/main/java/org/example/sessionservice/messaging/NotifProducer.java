package org.example.sessionservice.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotifProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${queues.notification}")
    private String notificationQueue;

    public NotifProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(String json) {
        jmsTemplate.convertAndSend(notificationQueue, json);
    }
}
