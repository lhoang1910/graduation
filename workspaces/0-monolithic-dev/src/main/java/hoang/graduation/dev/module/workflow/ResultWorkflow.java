package hoang.graduation.dev.module.workflow;

import hoang.graduation.dev.module.exam.doc.ExamSessionDoc;
import hoang.graduation.dev.module.exam.entity.ExamSessionEntity;
import hoang.graduation.dev.module.result.doc.ResultDetailDoc;
import hoang.graduation.dev.module.result.entity.ResultDetailEntity;
import hoang.graduation.dev.module.result.repo.ResultDetailESRepo;
import hoang.graduation.dev.module.result.repo.ResultDetailRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResultWorkflow {
    protected final transient Logger logger = LogManager.getLogger(this.getClass());
    private final ResultDetailRepo resultDetailRepo;
    private final ResultDetailESRepo resultDetailESRepo;

    @RabbitListener(queues = RabbitQueueMessage.QUEUE_SEND_SUBMIT_EXAM_SESSION)
    public void createExam(String id) {
        try {
            ResultDetailEntity result = resultDetailRepo.findById(id).orElse(null);
            if (result == null) {
                logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncResultEntityToDoc error ");
                return;
            }
            ResultDetailDoc doc = MappingUtils.mapObject(result, ResultDetailDoc.class);
//            doc.formatSearchingKeys();
            resultDetailESRepo.save(doc);
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> syncResultEntityToDocSuccess {} ", doc.getId());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>>>>> syncResultEntityToDoc error ", e);
        }
    }
}
