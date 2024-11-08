package hoang.graduation.dev.module.premium.service;

import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.premium.doc.PremiumPackageDoc;
import hoang.graduation.dev.module.premium.repo.PremiumPackageRepo;
import hoang.graduation.share.model.request.premium.CreatePremiumPackageRequest;
import hoang.graduation.share.model.response.WrapResponse;
import hoang.graduation.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumPackageService {
    private final PremiumPackageRepo premiumPackageRepo;
    private final LocalizationUtils localizationUtils;

    public WrapResponse<?> create(CreatePremiumPackageRequest request){
        if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getCode()) || request.getPrice() == null ||
                request.getLimitClassSlot() == null || request.getLimitPracticeTurn() == null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REQUIRED_FIELD_CODE_NAME_PRICE_LIMiT_SLOT))
                    .build();
        }
        PremiumPackageDoc doc = MappingUtils.mapObject(request, PremiumPackageDoc.class);
        doc.setId(UUID.randomUUID().toString());
        doc.formatSearchingKeys();
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_PREMIUM_SUCCESSFULLY))
                .data(premiumPackageRepo.save(doc))
                .build();
    }

    public WrapResponse<?> update(String id, CreatePremiumPackageRequest request){
        PremiumPackageDoc doc = premiumPackageRepo.findById(id).orElse(null);
        if (doc == null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PREMIUM_PACKAGE_NOT_FOUND))
                    .build();
        }
        if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getCode()) || request.getPrice() == null ||
                request.getLimitClassSlot() == null || request.getLimitPracticeTurn() == null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REQUIRED_FIELD_CODE_NAME_PRICE_LIMiT_SLOT))
                    .build();
        }
        doc = MappingUtils.mapObject(request, PremiumPackageDoc.class);
        doc.setId(UUID.randomUUID().toString());
        doc.formatSearchingKeys();
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_PREMIUM_SUCCESSFULLY))
                .data(premiumPackageRepo.save(doc))
                .build();
    }

    public WrapResponse<?> delete(String id){
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_PREMIUM_SUCCESSFULLY))
                .build();
    }
}
