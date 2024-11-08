package hoang.graduation.dev.module.workflow;

import hoang.graduation.dev.module.user.doc.UserDoc;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserESRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueueHandler {

    protected final transient Logger logger = LogManager.getLogger(this.getClass());

    protected Logger getLogger() {
        return this.logger;
    }

    private final UserESRepo userESRepo;

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_CREATE_USER)
    public void createUser(UserEntity user) {
        try {
            UserDoc doc = MappingUtils.mapObject(user, UserDoc.class);
            doc.formatSearchingKeys();
            userESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_UPDATE_USER)
    public void updateUser(UserEntity user) {
        try {
            UserDoc doc = userESRepo.findById(user.getId()).orElse(null);
            if (doc == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess error ");
            }
            doc = MappingUtils.mapObject(user, UserDoc.class);
            doc.formatSearchingKeys();
            userESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_UPDATE_STATUS_USER)
    public void updateStatus(UserEntity user) {
        try {
            UserDoc doc = userESRepo.findById(user.getId()).orElse(null);
            if (doc == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess error ");
            }
            userESRepo.save(MappingUtils.mapObject(user, UserDoc.class));
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncUserEntityToDocSuccess error ", e);
        }
    }
}
