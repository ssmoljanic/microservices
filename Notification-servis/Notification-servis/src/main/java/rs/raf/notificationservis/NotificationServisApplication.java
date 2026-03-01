package rs.raf.notificationservis;

import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class NotificationServisApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServisApplication.class, args);
    }

}
