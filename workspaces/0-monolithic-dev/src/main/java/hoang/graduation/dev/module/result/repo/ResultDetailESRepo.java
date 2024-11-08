package hoang.graduation.dev.module.result.repo;

import hoang.graduation.dev.module.result.doc.ResultDetailDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ResultDetailESRepo extends ElasticsearchRepository<ResultDetailDoc, String> {
}
