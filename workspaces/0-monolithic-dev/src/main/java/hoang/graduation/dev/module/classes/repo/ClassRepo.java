package hoang.graduation.dev.module.classes.repo;

import hoang.graduation.dev.module.classes.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassRepo extends JpaRepository<ClassEntity, String> {
    ClassEntity findByClassCode(String classCode);

    @Query("select classCode from ClassEntity where userEmails in :userEmail")
    List<String> findClassCodeByUserEmailIn(String userEmail);
}
