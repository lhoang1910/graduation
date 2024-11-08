package hoang.graduation.dev.module.classes.repo;

import hoang.graduation.dev.module.classes.doc.ClassDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ClassESRepo extends ElasticsearchRepository<ClassDoc, String> {
}
