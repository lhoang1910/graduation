package hoang.graduation.share.model.request.user;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SetNewPasswordRequest {

    @NotBlank(message = "{user.new_password.blank}")
    @Size(min = 8, message = "{user.new_password.size}")
    private String newPassword;

    @NotBlank(message = "{user.confirm_password.blank}")
    private String confirmPassword;

    @NotNull(message = "{user.token.null}")
    @Min(value = 100000, message = "{user.token.min}")
    private Integer token;
}
