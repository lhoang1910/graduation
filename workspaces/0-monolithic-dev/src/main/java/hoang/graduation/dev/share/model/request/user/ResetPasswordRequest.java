package hoang.graduation.dev.share.model.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest {
    private String userId;
    private String verificationCode;
    private String password;
    private String retypePassword;
}
