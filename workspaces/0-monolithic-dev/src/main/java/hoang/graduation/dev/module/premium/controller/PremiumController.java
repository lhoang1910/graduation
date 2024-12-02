package hoang.graduation.dev.module.premium.controller;

import hoang.graduation.dev.module.premium.service.PremiumService;
import hoang.graduation.dev.share.model.request.premium.BuyPremiumRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium-package")
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/buy")
    public WrapResponse<?> buy(@RequestBody BuyPremiumRequest request){
        return premiumService.buyPremium(request);
    }
}
