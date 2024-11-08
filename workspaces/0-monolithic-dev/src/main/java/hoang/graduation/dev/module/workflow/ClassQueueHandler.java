package hoang.graduation.dev.module.workflow;

import hoang.graduation.dev.module.classes.doc.ClassDoc;
import hoang.graduation.dev.module.classes.entity.ClassEntity;
import hoang.graduation.dev.module.classes.repo.ClassESRepo;
import hoang.graduation.dev.module.classes.repo.ClassRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassQueueHandler {
    protected final transient Logger logger = LogManager.getLogger(this.getClass());

    protected Logger getLogger() {
        return this.logger;
    }

    private final ClassESRepo classESRepo;
    private final ClassRepo classRepo;

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_CREATE_CLASS)
    public void createClass(String classId) {
        try {
            ClassEntity clss = classRepo.findById(classId).orElse(null);
            if (clss == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess error ");
                return;
            }
            ClassDoc doc = MappingUtils.mapObject(clss, ClassDoc.class);
            doc.formatSearchingKeys();
            doc.setParticipationAmount(clss.getUserEmails().size());
            classESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_UPDATE_CLASS)
    public void updateClass(String classId) {
        try {
            ClassEntity clss = classRepo.findById(classId).orElse(null);
            if (clss == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess error ");
                return;
            }
            ClassDoc doc = MappingUtils.mapObject(clss, ClassDoc.class);
            doc.formatSearchingKeys();
            doc.setParticipationAmount(clss.getUserEmails().size());
            classESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_DELETE_CLASS)
    public void deleteClass(String id) {
        try {
            classESRepo.deleteById(id);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess {} ", id);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_UPDATE_SLOT_CLASS)
    public void updateSlotClass(String id, int participantAmount) {
        try {
            ClassDoc doc = classESRepo.findById(id).orElse(null);
            if (doc == null) {
                return;
            }
            doc.setParticipationAmount(participantAmount);
            classESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess {} ", id);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncClassEntityToDocSuccess error ", e);
        }
    }
}
