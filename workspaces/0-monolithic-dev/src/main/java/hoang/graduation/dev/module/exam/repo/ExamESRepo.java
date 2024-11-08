package hoang.graduation.dev.module.exam.repo;

import hoang.graduation.dev.module.exam.doc.ExamDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ExamESRepo extends ElasticsearchRepository<ExamDoc, String> {
}
