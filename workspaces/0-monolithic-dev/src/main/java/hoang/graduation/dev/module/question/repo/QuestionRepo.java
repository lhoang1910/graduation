package hoang.graduation.dev.module.question.repo;

import hoang.graduation.dev.module.question.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<QuestionEntity, String> {
}
