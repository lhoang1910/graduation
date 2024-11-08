package hoang.graduation.share.model.request.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePremiumPackageRequest {
    private String code;
    private Long price;
    private String name;
    private String description;

    private Long limitClassSlot;
    private Long limitPracticeTurn;
}
