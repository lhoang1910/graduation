package hoang.graduation.dev.module.exam.repo;

import hoang.graduation.dev.module.exam.entity.ExamSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamSessionRepo extends JpaRepository<ExamSessionEntity, String> {

    @Query("select examCode from ExamSessionEntity where id = :examSessionId")
    String findExamCodeById(String examSessionId);

    @Query("select pointType from ExamSessionEntity where id = :examSessionId")
    int getPointTypeById(String examSessionId);

    @Query("select createdBy from ExamSessionEntity where id = :examSessionId")
    String findCreatedByById(String examSessionId);
}
