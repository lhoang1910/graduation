package hoang.graduation.dev.module.premium.repo;

import hoang.graduation.dev.module.premium.doc.PremiumLogDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;

public interface PremiumLogRepo extends ElasticsearchRepository<PremiumLogDoc, String> {
    List<PremiumLogDoc> findAllByActiveIsTrueAndExpiredDateBefore(Date date);
    PremiumLogDoc findFirstByUserEmailAndIsActiveTrue(String userEmail);
}
