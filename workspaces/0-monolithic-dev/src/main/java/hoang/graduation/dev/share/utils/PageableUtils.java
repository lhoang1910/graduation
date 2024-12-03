package hoang.graduation.dev.share.utils;

import hoang.graduation.dev.share.model.request.page.SortModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageableUtils {

    public static Pageable convertPageable(Integer pageNumber, Integer pageSize) {
        Pageable pageable;
        pageable = PageRequest.of(pageNumber, pageSize);
        return pageable;
    }

    public static Pageable convertPageableAndSort(Integer pageNumber, Integer pageSize, List<SortModel> sorts) {
        Sort sort = null;
        Pageable pageable;
        if (sorts != null && !sorts.isEmpty()) {
            for (SortModel item : sorts) {
                if (!item.getAsc()) {
                    sort = (sort == null) ? Sort.by(item.getSortBy()).descending() : sort.and(Sort.by(item.getSortBy()).descending());
                } else {
                    sort = (sort == null) ? Sort.by(item.getSortBy()) : sort.and(Sort.by(item.getSortBy()));
                }
            }
            pageable = PageRequest.of(pageNumber, pageSize, sort);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        return pageable;
    }
}
