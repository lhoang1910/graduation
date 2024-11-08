package hoang.graduation.dev.authentication;

import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionDetail {
    private final UserRepo userRepo;

    public UserDetailsService get(){
        return new org.springframework.security.core.userdetails.UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // TODO Auto-generated method stub
                return userRepo.findByEmail(username).orElseGet(() -> userRepo.findByPhoneNumber(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found " + username)));
            }
        };
    }
}
