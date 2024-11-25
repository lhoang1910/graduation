package hoang.graduation.dev.module.auth.controller;

import hoang.graduation.dev.module.auth.service.AuthService;
import hoang.graduation.dev.module.user.service.UserService;
import hoang.graduation.share.model.request.user.*;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public WrapResponse<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public WrapResponse<?> login(@Valid @RequestBody SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/forget-password")
    public WrapResponse<?> forgetPassword(@Valid @RequestBody RecoverPasswordRequest request) {
        return authService.forgetPassword(request);
    }

    @PostMapping("/reset-password")
    public WrapResponse<?> resetPassword(ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
