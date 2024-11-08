package hoang.graduation.share.model.request.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DateFilter {
    private String key;
    private Date startDate;
    private Date endDate;
}

