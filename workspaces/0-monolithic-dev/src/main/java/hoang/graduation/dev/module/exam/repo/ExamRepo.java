package hoang.graduation.dev.module.exam.repo;

import hoang.graduation.dev.module.exam.entity.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepo extends JpaRepository<ExamEntity, String> {
    ExamEntity findByCode(String examCode);
}
