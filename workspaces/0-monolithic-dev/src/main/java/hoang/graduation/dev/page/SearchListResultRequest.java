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
public class SearchListResultRequest extends BasePage{
    private String searchingKeys;

    private Date startAtFrom;
    private Date startAtTo;

    private Date submitAtFrom;
    private Date submitAtTo;
}
