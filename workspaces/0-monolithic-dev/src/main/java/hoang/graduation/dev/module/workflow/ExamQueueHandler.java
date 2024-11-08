package hoang.graduation.dev.module.workflow;

import hoang.graduation.dev.module.exam.doc.ExamDoc;
import hoang.graduation.dev.module.exam.entity.ExamEntity;
import hoang.graduation.dev.module.exam.repo.ExamESRepo;
import hoang.graduation.dev.module.exam.repo.ExamRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExamQueueHandler {
    protected final transient Logger logger = LogManager.getLogger(this.getClass());
    private final ExamRepo examRepo;
    private final ExamESRepo examESRepo;

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM)
    public void createExam(String examId) {
        try {
            ExamEntity exam = examRepo.findById(examId).orElse(null);
            if (exam == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDocSuccess error ");
                return;
            }
            ExamDoc doc = MappingUtils.mapObject(exam, ExamDoc.class);
            doc.formatSearchingKeys();
            doc.setQuestionAmount(exam.getQuestions().size());
            examESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDoc error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_UPDATE_EXAM)
    public void updateExam(String examId) {
        try {
            ExamEntity exam = examRepo.findById(examId).orElse(null);
            if (exam == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDocSuccess error ");
                return;
            }
            ExamDoc doc = MappingUtils.mapObject(exam, ExamDoc.class);
            doc.formatSearchingKeys();
            doc.setQuestionAmount(exam.getQuestions().size());
            examESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDoc error ", e);
        }
    }

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_DELETE_EXAM)
    public void deleteExam(String examId) {
        try {
            examESRepo.deleteById(examId);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDocSuccess {} ", examId);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncExamEntityToDoc error ", e);
        }
    }
}
