package hoang.graduation.dev.page;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchListUserRequest extends BasePage {
    private String searchingKeys;
    private Boolean isActive;
    private Integer role;
    private Integer gender;

    private Date birthDayFrom;
    private Date birthDayTo;

    private Date createdAtFrom;
    private Date createdAtTo;

    private Date updatedAtFrom;
    private Date updatedAtTo;
}
