package hoang.graduation.dev.module.schedule;

import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "hoang.config.schedule.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduleService {
    private final RabbitTemplate template;

    protected final transient Logger logger = LogManager.getLogger(this.getClass());
    protected Logger getLogger() {
        return this.logger;
    }

    @Scheduled(cron = "${hoang.config.schedule.disable-user}")
    public void disableUser() {
        getLogger().info(">>>>>>>>>>>>>>>>> disableUser <<<<<<<<<<<<<<<<");
        template.convertAndSend(RabbitQueueMessage.QUEUE_SEND_SCHEDULE_UPDATE_STATUS_USER, new Date());
    }

    @Scheduled(cron = "${hoang.config.schedule.disable-user}")
    public void expiratedPremium() {
        getLogger().info(">>>>>>>>>>>>>>>>> expiratePremium <<<<<<<<<<<<<<<<");
        template.convertAndSend(RabbitQueueMessage.QUEUE_SEND_SCHEDULE_PREMIUM_EXPIRATED, new Date());
    }
}
