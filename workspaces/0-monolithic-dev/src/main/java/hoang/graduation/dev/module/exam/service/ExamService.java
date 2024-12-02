package hoang.graduation.dev.module.exam.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.exam.entity.ExamEntity;
import hoang.graduation.dev.module.exam.repo.ExamRepo;
import hoang.graduation.dev.module.question.service.QuestionUtils;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.dev.share.model.object.QuestionModel;
import hoang.graduation.dev.share.model.request.exam.CreateExamRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import hoang.graduation.dev.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ExamService {

    private final LocalizationUtils localizationUtils;
    private final ExamRepo examRepo;
    private final QuestionUtils questionUtils;
    private final RabbitTemplate rabbitTemplate;

    public WrapResponse<?> createExam(CreateExamRequest request) {
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        ExamEntity newExam = MappingUtils.mapObject(request, ExamEntity.class);

        newExam.setId(UUID.randomUUID().toString());
        newExam.setCode(generateExamCode());
        newExam.setCreatedBy(crnt.getEmail());
        newExam.setUpdatedBy(crnt.getEmail());
        newExam.setCreatedDate(new Date());
        newExam.setUpdatedDate(new Date());
        request.setQuestions(MappingUtils.mapList(questionUtils.update(request.getQuestions()), QuestionModel.class));
        examRepo.save(newExam);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM, newExam.getId());
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_EXAM_SUCCESSFULLY))
                .build();
    }

    public WrapResponse<?> updateExam(String examId, CreateExamRequest request) {
        ExamEntity existExam = examRepo.findById(examId).orElse(null);
        if (existExam == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_NOT_FOUND))
                    .build();
        }
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        existExam.setName(request.getName());
        existExam.setDescription(request.getDescription());
        questionUtils.update(request.getQuestions());
        existExam.setQuestions(MappingUtils.mapList(questionUtils.update(request.getQuestions()), QuestionModel.class));
        existExam.setUpdatedBy(crnt.getEmail());
        existExam.setUpdatedDate(new Date());
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_EXAM, existExam.getId());
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_EXAM_SUCCESSFULLY))
                .build();
    }

    public WrapResponse<?> deleteExam(String id, boolean isDeleteAll) {
        ExamEntity exam = examRepo.findById(id).orElse(null);
        if (exam == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_NOT_FOUND))
                    .build();
        }
        if (isDeleteAll) {
            questionUtils.deleteAll(exam.getQuestions().stream().map(QuestionModel::getId).toList());
        }
        examRepo.delete(exam);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_DELETE_EXAM, id);
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_EXAM_SUCCESSFULLY))
                .build();
    }

    public String generateExamCode() {
        return "Q" + String.valueOf(Year.now().getValue()).substring(2) + "0" + examRepo.count() + 1;
    }
}
