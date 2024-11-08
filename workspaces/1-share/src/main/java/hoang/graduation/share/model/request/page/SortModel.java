package hoang.graduation.share.model.request.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SortModel {
    private Boolean asc;
    private String sortBy;
}
