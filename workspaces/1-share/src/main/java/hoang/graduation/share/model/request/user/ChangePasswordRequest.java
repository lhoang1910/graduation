package hoang.graduation.share.model.request.user;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "{user.old_password.blank}")
    private String oldPassword;

    @NotBlank(message = "{user.new_password.blank}")
    @Size(min = 8, message = "{user.new_password.size}")
    private String password;

    @NotBlank(message = "{user.confirm_password.blank}")
    private String retypePassword;
}
