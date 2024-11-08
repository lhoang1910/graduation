package hoang.graduation.dev.module.admin.controller;

import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.admin.doc.FeatureManagementDoc;
import hoang.graduation.dev.module.admin.repo.FeatureESRepo;
import hoang.graduation.share.model.request.feature.CreateFeatureRequest;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/feature")
public class FeatureController {
    private final FeatureESRepo featureESRepo;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/create")
    public WrapResponse<?> create(@RequestBody CreateFeatureRequest request) {
        if (StringUtils.isBlank(request.getFeatureCode()) || StringUtils.isBlank(request.getFeatureName()) || request.isEnable()){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REQUIRE_FEATURE_NAME_AND_FEATURE_CODE))
                    .build();
        }
        FeatureManagementDoc doc = featureESRepo.findByFeatureCode(request.getFeatureCode());
        if (doc != null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.FEATURE_HAS_EXISTED))
                    .build();
        }
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(featureESRepo.save(FeatureManagementDoc.builder()
                                .id(UUID.randomUUID().toString())
                        .featureCode(request.getFeatureCode())
                        .featureName(request.getFeatureName())
                        .description(request.getDescription())
                        .isEnable(request.isEnable())
                        .build()))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_FEATURE_SUCCESS))
                .build();
    }

    @PostMapping("/{id}/change-status")
    public WrapResponse<?> update(@PathVariable String id) {
        if (StringUtils.isBlank(id)){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.FEATURE_HAS_NOT_EXISTED))
                    .build();
        }
        FeatureManagementDoc doc = featureESRepo.findById(id).orElse(null);
        if (doc == null){
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.FEATURE_HAS_NOT_EXISTED))
                    .build();
        }
        doc.setEnable(!doc.isEnable());
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(featureESRepo.save(doc))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_FEATURE_SUCCESS))
                .build();
    }
}
