package hoang.graduation.dev.module.admin.controller;

import hoang.graduation.dev.module.admin.service.FeatureView;
import hoang.graduation.dev.module.classes.service.ClassView;
import hoang.graduation.dev.module.premium.service.PremiumLogView;
import hoang.graduation.dev.module.premium.service.PremiumPackageService;
import hoang.graduation.dev.module.premium.service.PremiumView;
import hoang.graduation.dev.module.user.query.QueryDetail;
import hoang.graduation.dev.page.*;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminViewController {
    private final QueryDetail queryDetail;
    private final ClassView classView;
    private final PremiumView premiumView;
    private final PremiumLogView premiumLogView;
    private final FeatureView featureView;

    @PostMapping("/users")
    public WrapResponse<?> getUserList(@RequestBody SearchListUserRequest request){
        return queryDetail.searchUserList(request);
    }

    @PostMapping("/classes")
    public WrapResponse<?> getAllClass(@RequestBody SearchListClassRequest request) {
        return classView.searchClassList(request);
    }

    @PostMapping("/premium-packages")
    public WrapResponse<?> getAllPremiumPackage(@RequestBody SearchListPremiumPackageRequest request) {
        return premiumView.searchPremiumPackageList(request);
    }

    @PostMapping("/premium-logs")
    public WrapResponse<?> getAllPremiumLog(@RequestBody SearchListPremiumLogRequest request) {
        return premiumLogView.searchListPremiumLog(request);
    }

    @PostMapping("/features")
    public WrapResponse<?> getAllFeature(@RequestBody SearchListFeatureRequest request) {
        return featureView.searchListFeatureManagement(request);
    }
}
