package hoang.graduation.dev.module.user.service;

import hoang.graduation.dev.component.EmailUtils;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.exceptions.DataNotFoundException;
import hoang.graduation.share.exceptions.InvalidPasswordException;
import hoang.graduation.share.model.request.user.*;
import hoang.graduation.share.model.response.WrapResponse;
import hoang.graduation.share.utils.MappingUtils;
import hoang.graduation.share.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final LocalizationUtils localizationUtils;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate template;
    private final UserRepo userRepo;
    private final EmailUtils emailUtils;
    private final RedisTemplate<String, String> redisTemplate;

    public WrapResponse<?> createUser(CreateUserRequest request){
        //register user
        if (!request.getEmail().isBlank() && userRepo.existsByEmail(request.getEmail())) {
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.USER_EMAIL_EXISTS)).build();
        }

        if (!ValidationUtils.isValidEmail(request.getEmail())) {
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_EMAIL)).build();
        }

        if (!request.getPassword().equals(request.getRetypePassword())) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build();
        }


        UserEntity newUser = MappingUtils.mapObject(request, UserEntity.class);
        newUser.setId(UUID.randomUUID().toString());
        newUser.setActive(true);
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());
        if (!request.isSocialLogin()) {
            String password = request.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        UserEntity savedUser = userRepo.save(newUser);
        template.convertAndSend(RabbitQueueMessage.QUEUE_SEND_CREATE_USER, savedUser);
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(savedUser)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ACCOUNT_REGISTRATION_SUCCESS))
                .build();
    }

    public UserEntity updateUser(String userId, UpdateUserRequest request) throws Exception {
        // Find the existing user by userId
        UserEntity existingUser = userRepo.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));

        if (StringUtils.isNotBlank(request.getFullName())) {
            existingUser.setFullName(request.getFullName());
        }
        if (request.getBirthDay() != null) {
            existingUser.setBirthDay(request.getBirthDay());
        }
        if (StringUtils.isNotBlank(request.getPhoneNumber()) && !userRepo.existsByPhoneNumber(request.getPhoneNumber())){
            existingUser.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.isGoogleAccountIdValid()) {
            existingUser.setGoogleAccountId(request.getGoogleAccountId());
        }
        UserEntity savedUser = userRepo.save(existingUser);
        template.convertAndSend(RabbitQueueMessage.QUEUE_SEND_UPDATE_USER, savedUser);
        return savedUser;
    }

    public void changePassword(String userId, ChangePasswordRequest request) throws Exception {
        UserEntity user = userRepo.findById(userId).orElseThrow(() -> new Exception(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_PASSWORD));
        }
        if (request.getPassword().equals(request.getRetypePassword())) {
            throw new RuntimeException(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    public void changeAvatar(String userId, String imageName) throws Exception {
        UserEntity existingUser = userRepo.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
        existingUser.setAvatar(imageName);
        userRepo.save(existingUser);
    }
}
