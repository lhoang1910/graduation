package hoang.graduation.dev.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchListPremiumPackageRequest extends BasePage{
    private String searchingKeys;
    private Long limitClassSlot;
    private Long limitPracticeTurn;
    private Long price;

    private Date createAtFrom;
    private Date createAtTo;
    private Date updateAtFrom;
    private Date updateAtTo;
}
