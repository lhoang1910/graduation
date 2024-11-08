package hoang.graduation.dev.component;

import hoang.graduation.dev.module.user.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public static UserEntity get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.getPrincipal() instanceof UserEntity selectedUser) {
            return (UserEntity) authentication.getPrincipal();
        }
        return null;
    }
}
