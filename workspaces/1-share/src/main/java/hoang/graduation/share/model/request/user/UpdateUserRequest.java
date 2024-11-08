package hoang.graduation.share.model.request.user;

import hoang.graduation.share.model.object.SocialAccountModel;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateUserRequest extends SocialAccountModel {

    private String fullName;
    private String phoneNumber;
    private int gender;
    private Date birthDay;
    private String googleAccountId;
}
