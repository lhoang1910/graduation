package hoang.graduation.dev.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchListExamSessionRequest extends BasePage{
    private String searchingKeys;

    private Date createdAtFrom;
    private Date createdAtTo;

    private Date updatedAtFrom;
    private Date updatedAtTo;
}
