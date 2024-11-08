package hoang.graduation.share.model.request.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyPremiumRequest {
    private String premiumPackageId;
    private int monthAmount;
}
