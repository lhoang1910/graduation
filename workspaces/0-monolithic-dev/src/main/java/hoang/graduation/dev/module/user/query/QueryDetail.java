package hoang.graduation.dev.module.user.query;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.module.user.doc.UserDoc;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.module.user.repo.UserRepo;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListUserRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class QueryDetail {
    private final UserRepo userRepo;
    private final SearchUtils searchUtils;

    public WrapResponse<?> getDetail(){
        UserEntity user = CurrentUser.get();
        boolean isSuccess = user != null;
        return WrapResponse.builder().isSuccess(isSuccess).status(isSuccess ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).data(CurrentUser.get()).build();
    }

    public WrapResponse<?> getDetail(String id){
        UserEntity user = userRepo.findById(id).orElse(null);
        boolean status = true;
        if (user == null){
            status = false;
        }
        return WrapResponse.builder()
                .isSuccess(status)
                .status(status ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .data(user)
                .build();
    }

    public WrapResponse<Object> searchUserList(SearchListUserRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (request.getSearchingKeys() != null && !request.getSearchingKeys().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("searchingKeys", request.getSearchingKeys()));
        }

        if (request.getIsActive() != null) {
            boolQuery.filter(QueryBuilders.termQuery("isActive", request.getIsActive()));
        }
        if (request.getRole() != null) {
            boolQuery.filter(QueryBuilders.termQuery("role", request.getRole()));
        }
        if (request.getGender() != null) {
            boolQuery.filter(QueryBuilders.termQuery("gender", request.getGender()));
        }

        if (request.getBirthDayFrom() != null && request.getBirthDayTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("birthDay")
                    .from(request.getBirthDayFrom()).to(request.getBirthDayTo()));
        }
        if (request.getCreatedAtFrom() != null && request.getCreatedAtTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("createdAt")
                    .from(request.getCreatedAtFrom()).to(request.getCreatedAtTo()));
        }
        if (request.getUpdatedAtFrom() != null && request.getUpdatedAtTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("updatedAt")
                    .from(request.getUpdatedAtFrom()).to(request.getUpdatedAtTo()));
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
                .data(searchUtils.search(searchQuery, UserDoc.class))
                .status(HttpStatus.OK)
                .build();
    }
}
