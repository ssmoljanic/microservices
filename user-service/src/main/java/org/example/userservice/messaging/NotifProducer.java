package org.example.userservice.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.jms.core.JmsTemplate;

@Component
public class NotifProducer {

    private final JmsTemplate jmsTemplate;
    private final String queue;

    public NotifProducer(JmsTemplate jmsTemplate,
                         @Value("${app.jms.notification-queue}") String queue) {
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    public void send(String json) {
        jmsTemplate.convertAndSend(queue, json);
    }
}
