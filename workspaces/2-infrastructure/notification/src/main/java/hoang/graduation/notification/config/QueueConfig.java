package hoang.graduation.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean
    public Queue sendNotification() {
        return QueueBuilder.durable("QUEUE_SEND_NOTIFICATION").build();
    }

    @Bean
    public Queue sendSystemNotification() {
        return QueueBuilder.durable("QUEUE_SEND_SYSTEM_NOTIFICATION").build();
    }
}
