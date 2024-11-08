package hoang.graduation.dev.module.exam.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.exam.entity.ExamSessionEntity;
import hoang.graduation.dev.module.exam.repo.ExamRepo;
import hoang.graduation.dev.module.exam.repo.ExamSessionRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.model.request.exam.session.CreateExamSessionRequest;
import hoang.graduation.share.model.response.WrapResponse;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExamSessionService {
    private final ExamRepo examRepo;
    private final ExamSessionRepo examSessionRepo;
    private final LocalizationUtils localizationUtils;
    private final RabbitTemplate rabbitTemplate;

    public WrapResponse<?> createExamSession(CreateExamSessionRequest request){
        if (StringUtils.isBlank(request.getExamCode()) || !examRepo.existsById(request.getExamId())){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_NOT_FOUND))
                    .build();
        }
        if (!request.isPublic() && CollectionUtils.isEmpty(request.getClassCodes())){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.MUST_ASSIGN_LEAST_A_CLASS))
                    .build();
        }
        ExamSessionEntity newExamSession = MappingUtils.mapObject(request, ExamSessionEntity.class);
        UserEntity crnt = CurrentUser.get();
        if (crnt == null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        newExamSession.setId(UUID.randomUUID().toString());
        newExamSession.setCreatedDate(new Date());
        newExamSession.setUpdatedDate(new Date());
        newExamSession.setCreatedBy(crnt.getEmail());
        newExamSession.setUpdatedBy(crnt.getEmail());
        ExamSessionEntity createdExamSession = examSessionRepo.save(newExamSession);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM_SESSION, createdExamSession.getId());
        return WrapResponse.builder()
                .isSuccess(true)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_EXAM_SESSION_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(createdExamSession)
                .build();
    }

    public WrapResponse<?> updateExamSession(String id, CreateExamSessionRequest request){
        if (StringUtils.isBlank(request.getExamCode()) || !examSessionRepo.existsById(id)){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_SESSION_NOT_FOUND))
                    .build();
        }
        if (StringUtils.isBlank(request.getExamCode()) || !examRepo.existsById(request.getExamId())){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_NOT_FOUND))
                    .build();
        }
        if (!request.isPublic() && CollectionUtils.isEmpty(request.getClassCodes())){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.MUST_ASSIGN_LEAST_A_CLASS))
                    .build();
        }
        ExamSessionEntity newExamSession = MappingUtils.mapObject(request, ExamSessionEntity.class);
        UserEntity crnt = CurrentUser.get();
        if (crnt == null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        newExamSession.setCreatedDate(new Date());
        newExamSession.setUpdatedDate(new Date());
        newExamSession.setCreatedBy(crnt.getEmail());
        newExamSession.setUpdatedBy(crnt.getEmail());
        ExamSessionEntity updatedExamSession = examSessionRepo.save(newExamSession);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM_SESSION, updatedExamSession.getId());
        return WrapResponse.builder()
                .isSuccess(true)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_EXAM_SESSION_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(updatedExamSession)
                .build();
    }

    public WrapResponse<?> deleteExamSession(String id) {
        if (!examSessionRepo.existsById(id)) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_SESSION_NOT_FOUND))
                    .build();
        }
        examSessionRepo.deleteById(id);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_EXAM_SESSION, id);
        return WrapResponse.builder()
                .isSuccess(true)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_EXAM_SESSION_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .build();
    }
}
