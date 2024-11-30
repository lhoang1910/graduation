package hoang.graduation.share.model.response.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportUserResponse {
    private int index;
    private String email;
    private String fullName;
    private Date dateOfBirth;
    private String password;

    private boolean success;
    private String errorDesc;
}
