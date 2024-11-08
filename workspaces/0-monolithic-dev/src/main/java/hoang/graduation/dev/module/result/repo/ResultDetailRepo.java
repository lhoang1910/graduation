package hoang.graduation.dev.module.result.repo;

import hoang.graduation.dev.module.result.entity.ResultDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResultDetailRepo extends JpaRepository<ResultDetailEntity, String> {
    @Query("select r from ResultDetailEntity r where r.studentId = :studentId and r.examSessionId = :examSessionId")
    ResultDetailEntity findByExamSessionIdAndStudentId(String examSessionId, String studentId);
}
