package hoang.graduation.dev.module.payment.repo;

import hoang.graduation.dev.module.payment.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepo extends JpaRepository<PaymentHistoryEntity, String> {
}
