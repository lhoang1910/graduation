package hoang.graduation.dev.module.exam.repo;

import hoang.graduation.dev.module.exam.doc.ExamSessionDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ExamSessionESRepo extends ElasticsearchRepository<ExamSessionDoc, String> {
}
