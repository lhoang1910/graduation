package hoang.graduation.dev.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasePage {
    private List<BaseSort> sortCriteria;
    private int pageSize;
    private int pageNumber;
}
