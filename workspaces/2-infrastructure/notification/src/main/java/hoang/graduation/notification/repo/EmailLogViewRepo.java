package hoang.graduation.notification.repo;

import hoang.graduation.notification.entity.EmailLogView;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmailLogViewRepo extends ElasticsearchRepository<EmailLogView, String> {
}
