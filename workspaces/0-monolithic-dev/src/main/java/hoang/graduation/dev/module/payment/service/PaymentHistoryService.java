package hoang.graduation.dev.module.payment.service;

import hoang.graduation.dev.module.payment.repo.PaymentHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepo paymentHistoryRepo;
}
