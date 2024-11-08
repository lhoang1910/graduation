package hoang.graduation.dev.module.result;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.exam.doc.ExamDoc;
import hoang.graduation.dev.module.result.doc.ResultDetailDoc;
import hoang.graduation.dev.module.result.entity.ResultDetailEntity;
import hoang.graduation.dev.module.result.repo.ResultDetailESRepo;
import hoang.graduation.dev.module.result.repo.ResultDetailRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListResultRequest;
import hoang.graduation.share.constant.Role;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ResultView {
    private final ResultDetailRepo resultDetailRepo;
    private final LocalizationUtils localizationUtils;
    private final SearchUtils searchUtils;

    public WrapResponse<?> getDetail(String id){
        ResultDetailEntity result = resultDetailRepo.findById(id).orElse(null);
        if (result == null){
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_RESULT_NOT_EXISTS)).build();
        }
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(result)
                .build();
    }

    public WrapResponse<?> getListResult(SearchListResultRequest request){
        UserEntity crnt = CurrentUser.get();
        if (crnt == null){
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.UNAUTHORIZED).message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)).build();
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (crnt.getRole() == Role.STUDENT){
            boolQuery.must(QueryBuilders.matchQuery("studentCode", crnt.getCode()));
        }
        if (crnt.getRole() == Role.TEACHER){
            boolQuery.must(QueryBuilders.matchQuery("examSessionAuthor", crnt.getEmail()));
        }
        if (request.getSearchingKeys() != null && !request.getSearchingKeys().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("searchingKeys", request.getSearchingKeys()));
        }

        if (request.getStartAtFrom() != null && request.getStartAtTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("startAt")
                    .from(request.getStartAtFrom()).to(request.getStartAtTo()));
        }
        if (request.getSubmitAtFrom() != null && request.getSubmitAtTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("submitAt")
                    .from(request.getSubmitAtFrom()).to(request.getSubmitAtTo()));
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
                .data(searchUtils.search(searchQuery, ResultDetailDoc.class))
                .status(HttpStatus.OK)
                .build();
    }
}
