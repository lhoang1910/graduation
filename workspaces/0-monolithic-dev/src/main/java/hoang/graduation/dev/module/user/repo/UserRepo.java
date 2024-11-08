package hoang.graduation.dev.module.user.repo;

import hoang.graduation.dev.module.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true and u.latestActive <= :latestActiveDate")
    List<UserEntity> findAllByActiveIsAndActiveDate(Date latestActiveDate);

    List<UserEntity> findAllByEmailIn(List<String> email);
}
