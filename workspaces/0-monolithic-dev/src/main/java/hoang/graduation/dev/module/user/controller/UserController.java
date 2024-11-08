package hoang.graduation.dev.module.user.controller;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.query.QueryDetail;
import hoang.graduation.dev.module.user.service.UserService;
import hoang.graduation.share.exceptions.InvalidPasswordException;
import hoang.graduation.share.model.request.user.ChangePasswordRequest;
import hoang.graduation.share.model.request.user.CreateUserRequest;
import hoang.graduation.share.model.request.user.UpdateUserRequest;
import hoang.graduation.share.model.response.WrapResponse;
import hoang.graduation.share.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final LocalizationUtils localizationUtils;
    private final UserService userService;
    private final QueryDetail queryDetail;

    @PostMapping("/create")
    public WrapResponse<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/{id}/update-profile")
    public ResponseEntity<WrapResponse<?>> updateUserDetails(@PathVariable String id, @RequestBody UpdateUserRequest request) throws Exception {
        UserEntity updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok().body(
                WrapResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_USER_DETAIL_SUCCESS))
                        .data(updatedUser)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("{id}/update-password")
    public ResponseEntity<WrapResponse<?>> resetPassword(@Valid @PathVariable String id, @Valid ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok(WrapResponse.builder()
                    .isSuccess(true)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.RESET_PASSWORD_SUCCESS))
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.ok(WrapResponse.builder()
                    .isSuccess(false)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_PASSWORD))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(WrapResponse.builder()
                    .isSuccess(false)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WrapResponse<?>> uploadProfileImage(
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        UserEntity loginUser = CurrentUser.get();

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    WrapResponse.builder()
                            .isSuccess(false)
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.IMAGE_REQUIRED))
                            .build()
            );
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(WrapResponse.builder()
                            .isSuccess(false)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.IMAGE_SIZE_EXCEEDS))
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .build());
        }

        // Check file type
        if (!FileUtils.isImageFile(file)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(WrapResponse.builder()
                            .isSuccess(false)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UNSUPPORTED_MEDIA_TYPE))
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .build());
        }

        // Store file and get filename
        String oldFileName = loginUser.getAvatar();
        String imageName = FileUtils.storeFile(file);

        userService.changeAvatar(loginUser.getId(), imageName);

        // Delete old file if exists
        if (!StringUtils.isEmpty(oldFileName)) {
            FileUtils.deleteFile(oldFileName);
        }

        return ResponseEntity.ok().body(WrapResponse.builder()
                .isSuccess(true)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_PROFILE_IMAGE_SUCCESS))
                .status(HttpStatus.CREATED)
                .data(imageName)
                .build());
    }

    @GetMapping("/avatar/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/default-profile-image.jpeg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{id}")
    public WrapResponse<?> getDetail(@PathVariable String id) {
        return queryDetail.getDetail(id);
    }
}
