package hoang.graduation.dev.share.model.request.user;

import hoang.graduation.dev.share.model.object.SocialAccountModel;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateUserRequest extends SocialAccountModel {

    private String email;
    private String password;
    private String retypePassword;
}
