package hoang.graduation.notification.repo;

import hoang.graduation.notification.entity.NotificationView;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NotificationViewRepo extends ElasticsearchRepository<NotificationView, String> {
    long countByUserIdAndStatus(String userId, String status);
    long countByUserIdAndStatusAndSystem(String userId, String status,String system);
}
