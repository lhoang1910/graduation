package hoang.graduation.dev.module.premium.service;

import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.module.premium.doc.PremiumLogDoc;
import hoang.graduation.dev.module.premium.repo.PremiumLogRepo;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListPremiumLogRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
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

@RequiredArgsConstructor
@Service
public class PremiumLogView {
    private final SearchUtils searchUtils;

    public WrapResponse<Object> searchListPremiumLog(SearchListPremiumLogRequest request) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (request.getSearchingKeys() != null && !request.getSearchingKeys().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("searchingKeys", request.getSearchingKeys()));
        }

        if (request.getPremiumCode() != null) {
            boolQuery.filter(QueryBuilders.termQuery("premiumCode", request.getPremiumCode()));
        }

        if (request.getIsActive() != null) {
            boolQuery.filter(QueryBuilders.termQuery("isActive", request.getIsActive()));
        }

        if (request.getBoughtDateFrom() != null && request.getBoughtDateTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("boughtDate")
                    .from(request.getBoughtDateFrom()).to(request.getBoughtDateTo()));
        }

        if (request.getExpiredDateFrom() != null && request.getExpiredDateTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("expiredDate")
                    .from(request.getExpiredDateFrom()).to(request.getExpiredDateTo()));
        }

        // Xử lý sắp xếp
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
                .data(searchUtils.search(searchQuery, PremiumLogDoc.class))
                .status(HttpStatus.OK)
                .build();
    }

}
