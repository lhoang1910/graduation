package hoang.graduation.dev.module.admin.action;

import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAction {
    private final UserRepo userRepo;
    private final LocalizationUtils localizationUtils;
    private final RabbitTemplate rabbitTemplate;

    public WrapResponse<?> changeUserStatus(String userId) {
        UserEntity user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return WrapResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .isSuccess(false)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        user.setActive(!user.isActive());
        userRepo.save(user);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_STATUS_USER, user);
        return WrapResponse.builder()
                .status(HttpStatus.OK)
                .isSuccess(true)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                .build();
    }
}
