package hoang.graduation.dev.module.admin.repo;

import hoang.graduation.dev.module.admin.doc.FeatureManagementDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FeatureESRepo extends ElasticsearchRepository<FeatureManagementDoc, String> {
    FeatureManagementDoc findByFeatureCode(String code);
}
