package hoang.graduation.dev.module.premium.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.config.VNPayConfig;
import hoang.graduation.dev.config.VNPaymentUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.premium.doc.PremiumLogDoc;
import hoang.graduation.dev.module.premium.doc.PremiumPackageDoc;
import hoang.graduation.dev.module.premium.repo.PremiumLogRepo;
import hoang.graduation.dev.module.premium.repo.PremiumPackageRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.dev.share.model.request.premium.BuyPremiumRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumLogRepo premiumLogRepo;
    private final PremiumPackageRepo premiumPackageRepo;
    private final UserRepo userRepo;
    private final LocalizationUtils localizationUtils;
    private final VNPaymentUtils paymentUtils;

    public WrapResponse<?> buyPremium(BuyPremiumRequest request) {
        PremiumPackageDoc doc = premiumPackageRepo.findById(request.getPremiumPackageId()).orElse(null);
        if (doc == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PREMIUM_PACKAGE_NOT_FOUND))
                    .build();
        }
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }

        Long totalAmount = doc.getPrice() * request.getMonthAmount();

        paymentUtils.createOrder(totalAmount, "", VNPayConfig.vnp_apiUrl);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, request.getMonthAmount());

        PremiumLogDoc logDoc = PremiumLogDoc.builder()
                .id(UUID.randomUUID().toString())
                .premiumCode(doc.getCode())
                .premiumName(doc.getName())
                .boughtDate(new Date())
                .expiredDate(calendar.getTime())
                .limitClassSlot(doc.getLimitClassSlot())
                .limitPracticeTurn(doc.getLimitPracticeTurn())
                .monthAmount(request.getMonthAmount())
                .userEmail(crnt.getEmail())
                .buyerName(crnt.getFullName())
                .totalAmount(totalAmount)
                .isActive(true)
                .build();
        logDoc.formatSearchingKeys();
        crnt.setPremiumCode(doc.getCode());
        premiumLogRepo.save(logDoc);
        userRepo.save(crnt);
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.BUY_PREMIUM_SUCCESSFULLY))
                .data(logDoc)
                .build();
    }
}
