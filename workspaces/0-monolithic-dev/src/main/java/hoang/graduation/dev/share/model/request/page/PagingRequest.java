package hoang.graduation.dev.share.model.request.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@AllArgsConstructor
public class PagingRequest {
    private Integer pageNumber;
    private Integer pageSize;
    private String keyword;
    private SortModel[] sortBys;
    private Filter[] filters;
    private DateFilter[] dateFilters;

    public PagingRequest() {
        this.pageNumber = 0;
        this.pageSize = 10;
        this.sortBys = new SortModel[]{new SortModel(true, "id")};
        this.filters = new Filter[]{};
        this.dateFilters = new DateFilter[]{};
    }

    public Sort getSort() {
        if (sortBys == null || sortBys.length == 0) {
            return Sort.unsorted();
        }
        Sort.Order[] orders = new Sort.Order[sortBys.length];
        for (int i = 0; i < sortBys.length; i++) {
            orders[i] = new Sort.Order(sortBys[i].getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, sortBys[i].getSortBy());
        }
        return Sort.by(orders);
    }

}

