package hoang.graduation.dev.module.admin.service;

import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.module.admin.doc.FeatureManagementDoc;
import hoang.graduation.dev.module.admin.repo.FeatureESRepo;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListFeatureRequest;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureView {
    private final FeatureESRepo featureESRepo;
    private SearchUtils searchUtils;

    public WrapResponse<Object> searchListFeatureManagement(SearchListFeatureRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        List<Sort.Order> orders = new ArrayList<>();
        if (request.getSortCriteria() != null) {
            for (BaseSort sort : request.getSortCriteria()) {
                Sort.Order order = sort.isAsc() ? Sort.Order.asc(sort.getKey()) : Sort.Order.desc(sort.getKey());
                orders.add(order);
            }
        }
        Sort sort = Sort.by(orders);

        PageRequest pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageRequest)
                .build();

        return WrapResponse.builder()
                .isSuccess(true)
                .data(searchUtils.search(searchQuery, FeatureManagementDoc.class))
                .status(HttpStatus.OK)
                .build();
    }

}
