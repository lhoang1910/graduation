package hoang.graduation.dev.module.admin.controller;

import hoang.graduation.dev.module.premium.service.PremiumPackageService;
import hoang.graduation.share.model.request.premium.CreatePremiumPackageRequest;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/premium-package")
public class PremiumPackageController {

    private final PremiumPackageService premiumPackageService;

    @PostMapping("/create")
    public WrapResponse<?> create(@RequestBody CreatePremiumPackageRequest request) {
        return premiumPackageService.create(request);
    }

    @PostMapping("/{id}/update")
    public WrapResponse<?> update(@PathVariable String id, @RequestBody CreatePremiumPackageRequest request) {
        return premiumPackageService.update(id, request);
    }

    @PostMapping("/{id}/delete")
    public WrapResponse<?> delete(@PathVariable String id) {
        return premiumPackageService.delete(id);
    }
}
