package hoang.graduation.dev.module.workflow;

import hoang.graduation.dev.module.exam.doc.ExamSessionDoc;
import hoang.graduation.dev.module.exam.entity.ExamSessionEntity;
import hoang.graduation.dev.module.exam.repo.ExamSessionESRepo;
import hoang.graduation.dev.module.exam.repo.ExamSessionRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExamSessionQueueHandler {

    protected final transient Logger logger = LogManager.getLogger(this.getClass());
    private final ExamSessionRepo examSessionRepo;
    private final ExamSessionESRepo examSessionESRepo;

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM_SESSION)
    public void createExam(String examId) {
        try {
            ExamSessionEntity exam = examSessionRepo.findById(examId).orElse(null);
            if (exam == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess error ");
                return;
            }
            ExamSessionDoc doc = MappingUtils.mapObject(exam, ExamSessionDoc.class);
            doc.formatSearchingKeys();
            examSessionESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_UPDATE_EXAM_SESSION)
    public void updateExam(String examId) {
        try {
            ExamSessionEntity exam = examSessionRepo.findById(examId).orElse(null);
            if (exam == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess error ");
                return;
            }
            ExamSessionDoc doc = MappingUtils.mapObject(exam, ExamSessionDoc.class);
            doc.formatSearchingKeys();
            examSessionESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_DELETE_EXAM_SESSION)
    public void deleteExam(String examId) {
        try {
            examSessionESRepo.deleteById(examId);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess {} ", examId);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamSessionEntityToDocSuccess error ", e);
        }
    }
}
