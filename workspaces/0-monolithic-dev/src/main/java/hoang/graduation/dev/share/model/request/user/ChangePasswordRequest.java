package hoang.graduation.dev.share.model.request.user;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    private String oldPassword;
    private String password;
    private String retypePassword;
}
