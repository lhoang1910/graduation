package hoang.graduation.dev.module.user.repo;

import hoang.graduation.dev.module.user.doc.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserESRepo extends ElasticsearchRepository<UserDoc, String> {
}
