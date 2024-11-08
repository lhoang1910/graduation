package hoang.graduation.dev.page;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchListPremiumLogRequest extends BasePage{
    private String searchingKeys;
    private String userEmail;
    private String buyerName;
    private String premiumCode;
    private String premiumName;
    private Integer monthAmount;
    private Long totalAmount;
    private Long limitClassSlot;
    private Long limitPracticeTurn;

    private Date boughtDateFrom;
    private Date boughtDateTo;
    private Date expiredDateFrom;
    private Date expiredDateTo;

    private Boolean isActive;
}
