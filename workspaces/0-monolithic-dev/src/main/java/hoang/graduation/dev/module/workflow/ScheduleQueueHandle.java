package hoang.graduation.dev.module.workflow;

import hoang.graduation.dev.module.premium.doc.PremiumLogDoc;
import hoang.graduation.dev.module.premium.repo.PremiumLogRepo;
import hoang.graduation.dev.module.user.doc.UserDoc;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserESRepo;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ScheduleQueueHandle {

    protected final transient Logger logger = LogManager.getLogger(this.getClass());

    protected Logger getLogger() {
        return this.logger;
    }

    private final UserRepo userRepo;
    private final UserESRepo userESRepo;
    private final PremiumLogRepo premiumLogRepo;

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_SCHEDULE_UPDATE_STATUS_USER)
    public void autoDisableUser() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -60);
            Date ruleDate = calendar.getTime();
            userESRepo.saveAll(userRepo.findAllByActiveIsAndActiveDate(ruleDate)
                    .stream()
                    .peek(x -> x.setActive(false))
                    .map(item -> MappingUtils.mapObject(item, UserDoc.class))
                    .toList());

            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess");
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_SCHEDULE_PREMIUM_EXPIRATED)
    public void autoExpirePremiumUser() {
        try {
            List<PremiumLogDoc> logDocs = premiumLogRepo.findAllByActiveIsTrueAndExpiredDateBefore(new Date()).stream()
                    .peek(x -> x.setActive(false))
                    .map(item -> MappingUtils.mapObject(item, PremiumLogDoc.class))
                    .toList();
            premiumLogRepo.saveAll(logDocs);
            List<UserEntity> users = userRepo.findAllByEmailIn(logDocs.stream().map(PremiumLogDoc::getUserEmail).toList());
            logDocs.forEach(x -> x.setActive(true));
            users.forEach(x -> x.setPremiumCode(null));
            userRepo.saveAll(users);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> auto disable premium when expiration <<<<<<<<<<<<<<<");
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> auto disable premium when expiration  error ", e);
        }
    }
}
