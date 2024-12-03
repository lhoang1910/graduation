package hoang.graduation.dev.share.model.response.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String message;
    private String token;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private String id;
    private String username;

    private List<String> roles;
}
