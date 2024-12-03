package hoang.graduation.dev.share.model.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SocialAccountModel {
    protected String facebookAccountId;
    protected String googleAccountId;

    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }

    public boolean isSocialLogin() {
        return (facebookAccountId != null && !facebookAccountId.isEmpty()) ||
                (googleAccountId != null && !googleAccountId.isEmpty());
    }
}
