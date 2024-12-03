package hoang.graduation.dev.share.model.request.user;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ImportUserRequest {
    private int index;
    private String email;
    private String password;
    private String fullName;
    private Date dateOfBirth;
}
