package hoang.graduation.dev.share.model.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInRequest {

    private String phoneNumber;

    private String email;

    private String password;

    private int role;

    // Facebook Account Id, not mandatory, can be blank
    private String facebookAccountId;

    // Google Account Id, not mandatory, can be blank
    private String googleAccountId;

    //For Google, Facebook login
    // Full name, not mandatory, can be blank
    private String fullname;

    // Profile image URL, not mandatory, can be blank
    private String avatar;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
    // Kiểm tra facebookAccountId có hợp lệ không
    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    // Kiểm tra googleAccountId có hợp lệ không
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
