package hoang.graduation.dev.page;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchListClassRequest extends BasePage {
    private String userId;
    private String searchingKeys;
    private Integer limitSlot;
    private Integer practiceAmount;
    private Integer examineAmount;
    private Date createAtFrom;
    private Date createAtTo;
    private Date updateAtFrom;
    private Date updateAtTo;
}
