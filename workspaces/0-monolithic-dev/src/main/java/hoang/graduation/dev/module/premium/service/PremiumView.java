package hoang.graduation.dev.module.premium.service;

import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.module.premium.doc.PremiumPackageDoc;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListPremiumPackageRequest;
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
public class PremiumView {

    private final SearchUtils searchUtils;

    public WrapResponse<Object> searchPremiumPackageList(SearchListPremiumPackageRequest request) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (request.getSearchingKeys() != null && !request.getSearchingKeys().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("searchingKeys", request.getSearchingKeys()));
        }

//        if (request.getLimitClassSlot() != null) {
//            boolQuery.filter(QueryBuilders.termQuery("limitClassSlot", request.getLimitClassSlot()));
//        }
//        if (request.getLimitPracticeTurn() != null) {
//            boolQuery.filter(QueryBuilders.termQuery("limitPracticeTurn", request.getLimitPracticeTurn()));
//        }
//        if (request.getPrice() != null) {
//            boolQuery.filter(QueryBuilders.termQuery("price", request.getPrice()));
//        }

        if (request.getCreateAtFrom() != null && request.getCreateAtTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("createAt")
                    .from(request.getCreateAtFrom()).to(request.getCreateAtTo()));
        }
        if (request.getUpdateAtFrom() != null && request.getUpdateAtTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("updateAt")
                    .from(request.getUpdateAtFrom()).to(request.getUpdateAtTo()));
        }

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
                .data(searchUtils.search(searchQuery, PremiumPackageDoc.class))
                .status(HttpStatus.OK)
                .build();
    }

}
