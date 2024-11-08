package hoang.graduation.dev.module.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import hoang.graduation.share.constant.time.DateTimeFomartter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "accounts", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_phoneNumber", columnList = "phoneNumber"),
        @Index(name = "idx_isActive_latestActive", columnList = "isActive,latestActive")
})
public class UserEntity implements UserDetails, OAuth2User {
    @Id
    private String id;
    private String code;
    private String avatar;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private int gender;
    @JsonFormat(pattern = DateTimeFomartter.DATE_VN)
    private Date birthDay;
    private int role;
    @Column(columnDefinition = "boolean default true")
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private Date latestActive;
    private String premiumCode;

    @Column(name = "google_account_id")
    private String googleAccountId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_"+String.valueOf(role)));
        return authorityList;
    }
    @Override
    public String getUsername() {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return phoneNumber;
        } else if (email != null && !email.isEmpty()) {
            return email;
        }
        return "";
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //Login facebook
    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<String, Object>();
    }
    @Override
    public String getName() {
        return getAttribute("name");
    }

}
