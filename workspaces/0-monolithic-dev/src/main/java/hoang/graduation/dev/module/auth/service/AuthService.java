package hoang.graduation.dev.module.auth.service;

import hoang.graduation.dev.component.EmailUtils;
import hoang.graduation.dev.component.JwtUtils;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.dev.share.model.request.user.RecoverPasswordRequest;
import hoang.graduation.dev.share.model.request.user.ResetPasswordRequest;
import hoang.graduation.dev.share.model.request.user.SignInRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import hoang.graduation.dev.share.model.response.user.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;
    private final JwtUtils jwtUtils;
    private final EmailUtils emailUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    public WrapResponse<?> signIn(SignInRequest signInRequest) {
        Optional<UserEntity> user = null;
        if (StringUtils.isNotBlank(signInRequest.getEmail())) {
            user = userRepo.findByEmail(signInRequest.getEmail());
            if (user.isEmpty()) {
                return WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                        .build();
            }
        }
        if (StringUtils.isNotBlank(signInRequest.getPhoneNumber())) {
            user = userRepo.findByPhoneNumber(signInRequest.getPhoneNumber());
            if (user.isEmpty()) {
                return WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                        .build();
            }
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        user.get().setLatestActive(new Date());
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .data(LoginResponse.builder()
                        .id(user.get().getId())
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                        .token(jwtUtils.generateToken(user.get()))
                        .refreshToken(jwtUtils.generateRefreshToken(new HashMap<>(), user.get()))
                        .username(user.get().getUsername())
                        .build())
                .build();
    }

    public WrapResponse<?> forgetPassword(RecoverPasswordRequest request) {
        UserEntity user = userRepo.findByEmail(request.getIdentifier())
                .or(() -> userRepo.findByPhoneNumber(request.getIdentifier())).orElse(null);
        if (user == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        long codeExpiration = 300;
        redisTemplate.opsForValue().set(user.getId(), verificationCode, codeExpiration, TimeUnit.MINUTES);

        String subject = "Your Verification Code";
        String text = "Your verification code is: " + verificationCode;
        emailUtils.sendEmail(user.getEmail(), subject, text);
        return WrapResponse.<String>builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.VERIFICATION_CODE_SENT_SUCCESSFULLY))
                .data(user.getId())
                .build();
    }

    public WrapResponse<?> resetPassword(ResetPasswordRequest request) {
        UserEntity user = userRepo.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return WrapResponse.<String>builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }

        String cachedCode = redisTemplate.opsForValue().get(request.getUserId());
        if (cachedCode == null || !cachedCode.equals(request.getVerificationCode())) {
            return WrapResponse.<String>builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_VERIFICATION_CODE))
                    .build();
        }

        if (!request.getPassword().equals(request.getRetypePassword())) {
            return WrapResponse.<String>builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);

        redisTemplate.delete(request.getUserId());

        return WrapResponse.<String>builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RESET_PASSWORD_SUCCESS))
                .build();
    }
}
