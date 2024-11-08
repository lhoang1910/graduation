package hoang.graduation.dev.module.classes.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.admin.doc.FeatureManagementDoc;
import hoang.graduation.dev.module.admin.repo.FeatureESRepo;
import hoang.graduation.dev.module.classes.entity.ClassEntity;
import hoang.graduation.dev.module.classes.repo.ClassRepo;
import hoang.graduation.dev.module.premium.doc.PremiumLogDoc;
import hoang.graduation.dev.module.premium.repo.PremiumLogRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.share.constant.FeatureCode;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.model.request.classes.CreateClassRequest;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepo classRepo;
    private final UserRepo userRepo;
    private final RabbitTemplate rabbitTemplate;
    private final LocalizationUtils localizationUtils;
    private final PremiumLogRepo logRepo;
    private final FeatureESRepo featureESRepo;

    public WrapResponse<?> create(CreateClassRequest request) {
        ClassEntity existClass = classRepo.findByClassCode(request.getClassCode());
        if (existClass != null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_CODE_EXISTED))
                    .build();
        }
        UserEntity currentUser = CurrentUser.get();
        assert currentUser != null;
        PremiumLogDoc logDoc = logRepo.findFirstByUserEmailAndIsActiveTrue(currentUser.getEmail());
        int limit = 0;
        if (logDoc != null) {
            limit = Math.toIntExact(logDoc.getLimitClassSlot());
        }
        ClassEntity savedClass = classRepo.save(ClassEntity.builder()
                .id(UUID.randomUUID().toString())
                .classCode(request.getClassCode())
                .className(request.getClassName())
                .userEmails(new ArrayList<>())
                .createAt(new Date())
                .updateAt(new Date())
                .createdByEmail(currentUser.getEmail())
                .createdByName(currentUser.getFullName())
                .limitSlot(limit)
                .build());
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_CLASS, savedClass.getId());
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_CLASS_SUCCESS))
                .data(savedClass)
                .build();
    }

    public WrapResponse<?> update(String id, CreateClassRequest request) {
        ClassEntity existClass = classRepo.findById(id).orElse(null);
        if (existClass != null) {
            existClass.setClassName(request.getClassName());
            existClass.setUpdateAt(new Date());
            ClassEntity savedClass = classRepo.save(existClass);
            rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_CLASS, savedClass.getId());
            return WrapResponse.builder()
                    .isSuccess(true)
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CLASS_SUCCESS))
                    .data(existClass)
                    .build();
        }
        return WrapResponse.builder()
                .isSuccess(false)
                .status(HttpStatus.BAD_REQUEST)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_NOT_EXISTED))
                .build();
    }

    public WrapResponse<?> delete(String id) {
        ClassEntity existClass = classRepo.findById(id).orElse(null);
        if (existClass != null) {
            classRepo.delete(existClass);
            rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_CLASS, id);
            return WrapResponse.builder()
                    .isSuccess(true)
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLASS_SUCCESS))
                    .build();
        }
        return WrapResponse.builder()
                .isSuccess(false)
                .status(HttpStatus.BAD_REQUEST)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_NOT_EXISTED))
                .build();
    }

    public WrapResponse<?> joinClass(String classId) {
        ClassEntity existClass = classRepo.findById(classId).orElse(null);
        if (existClass != null) {
            UserEntity currentUser = CurrentUser.get();
            if (currentUser != null) {
                FeatureManagementDoc doc = featureESRepo.findByFeatureCode(FeatureCode.PREMIUM);
                if (existClass.getLimitSlot() < existClass.getUserEmails().size()+1 && doc != null && doc.isEnable()){
                    return WrapResponse.builder()
                            .isSuccess(false)
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_HAS_MAX_SLOT))
                            .build();
                }
                existClass.getUserEmails().add(currentUser.getEmail());
                rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_SLOT_CLASS, existClass.getId(), existClass.getUserEmails().size());
                return WrapResponse.builder()
                        .isSuccess(true)
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.JOIN_CLASS_SUCCESS))
                        .data(classRepo.save(existClass))
                        .build();
            }
        }
        return WrapResponse.builder()
                .isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_NOT_FOUND)).build();
    }

    public WrapResponse<?> addToClass(String email, String classId) {
        ClassEntity existClass = classRepo.findById(classId).orElse(null);
        if (existClass != null) {
            UserEntity user = userRepo.findByEmail(email).orElse(null);
            if (user == null) {
                rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_CLASS, existClass);
                return WrapResponse.builder()
                        .isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)).build();
            }
            FeatureManagementDoc doc = featureESRepo.findByFeatureCode(FeatureCode.PREMIUM);
            if (existClass.getLimitSlot() < existClass.getUserEmails().size()+1 && doc != null && doc.isEnable()){
                return WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_HAS_MAX_SLOT))
                        .build();
            }
            existClass.getUserEmails().add(user.getEmail());
            rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_SLOT_CLASS, existClass.getId(), existClass.getUserEmails().size());
            return WrapResponse.builder()
                    .isSuccess(true)
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.JOIN_CLASS_SUCCESS))
                    .data(classRepo.save(existClass))
                    .build();
        }
        return WrapResponse.builder()
                .isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.JOIN_CLASS_FAILED)).build();
    }

    public WrapResponse<?> deleteFromClass(String email, String classId) {
        ClassEntity existClass = classRepo.findById(classId).orElse(null);
        if (existClass != null) {
            if (existClass.getUserEmails().contains(email)) {
                existClass.getUserEmails().remove(email);
                rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_SLOT_CLASS, existClass.getId(), existClass.getUserEmails().size());
                return WrapResponse.builder()
                        .isSuccess(true)
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.REMOVE_USER_FROM_CLASS_SUCCESS))
                        .data(classRepo.save(existClass))
                        .build();
            } else {
                return WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_IN_CLASS))
                        .build();
            }
        }
        return WrapResponse.builder()
                .isSuccess(false)
                .status(HttpStatus.BAD_REQUEST)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_NOT_FOUND))
                .build();
    }

    public WrapResponse<?> outClass(String classId) {
        ClassEntity existClass = classRepo.findById(classId).orElse(null);
        if (existClass != null) {
            UserEntity currentUser = CurrentUser.get();
            if (currentUser != null && existClass.getUserEmails().contains(currentUser.getEmail())) {
                existClass.getUserEmails().remove(currentUser.getEmail());
                rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_SLOT_CLASS, existClass.getId(), existClass.getUserEmails().size());
                return WrapResponse.builder()
                        .isSuccess(true)
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.OUT_CLASS_SUCCESS))
                        .data(classRepo.save(existClass))
                        .build();
            } else {
                return WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_IN_CLASS))
                        .build();
            }
        }
        return WrapResponse.builder()
                .isSuccess(false)
                .status(HttpStatus.BAD_REQUEST)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CLASS_NOT_FOUND))
                .build();
    }
}
