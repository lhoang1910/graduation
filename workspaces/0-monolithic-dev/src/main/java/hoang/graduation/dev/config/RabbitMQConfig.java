package hoang.graduation.dev.config;

import hoang.graduation.dev.share.constant.rm.RabbitQueueMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {
    @Bean
    Queue createUser() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_CREATE_USER).build();
    }

    @Bean
    Queue updateUser() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_UPDATE_USER).build();
    }

    @Bean
    Queue updateUserStatus() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_UPDATE_STATUS_USER).build();
    }

    @Bean
    Queue autoDisableUser() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_SCHEDULE_UPDATE_STATUS_USER).build();
    }

    @Bean
    Queue createClass() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_CREATE_CLASS).build();
    }

    @Bean
    Queue updateClass() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_UPDATE_CLASS).build();
    }

    @Bean
    Queue deleteClass() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_DELETE_CLASS).build();
    }

    @Bean
    Queue expirationPremium() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_SCHEDULE_PREMIUM_EXPIRATED).build();
    }

    @Bean
    Queue updateClassSlot() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_UPDATE_SLOT_CLASS).build();
    }

    @Bean
    Queue createExam() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM).build();
    }

    @Bean
    Queue updateExam() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_UPDATE_EXAM).build();
    }

    @Bean
    Queue deleteExam() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_DELETE_EXAM).build();
    }

    @Bean
    Queue createExamSession() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM_SESSION).build();
    }

    @Bean
    Queue updateExamSession() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_UPDATE_EXAM_SESSION).build();
    }

    @Bean
    Queue deleteExamSession() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_DELETE_EXAM_SESSION).build();
    }

    @Bean
    Queue submitExam() {
        return QueueBuilder.durable(RabbitQueueMessage.QUEUE_SEND_SUBMIT_EXAM_SESSION).build();
    }
}
