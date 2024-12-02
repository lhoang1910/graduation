package hoang.graduation.dev.module.admin.controller;

import hoang.graduation.dev.module.admin.action.UserAction;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminActionController {
    private final UserAction adminAction;

    @PostMapping("/users/{id}/change-status")
    public WrapResponse<?> changeStatus(@PathVariable String id) {
        return adminAction.changeUserStatus(id);
    }
}
