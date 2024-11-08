package hoang.graduation.dev.module.premium.repo;

import hoang.graduation.dev.module.premium.doc.PremiumPackageDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PremiumPackageRepo extends ElasticsearchRepository<PremiumPackageDoc, String> {
}
