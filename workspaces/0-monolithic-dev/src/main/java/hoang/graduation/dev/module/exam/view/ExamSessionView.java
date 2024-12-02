package hoang.graduation.dev.module.exam.view;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.component.FinderUtils;
import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.exam.doc.ExamDoc;
import hoang.graduation.dev.module.exam.doc.ExamSessionDoc;
import hoang.graduation.dev.module.exam.entity.ExamSessionEntity;
import hoang.graduation.dev.module.exam.repo.ExamSessionESRepo;
import hoang.graduation.dev.module.exam.repo.ExamSessionRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListExamSessionRequest;
import hoang.graduation.dev.share.constant.Role;
import hoang.graduation.dev.share.model.response.WrapResponse;
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

@Component
@RequiredArgsConstructor
public class ExamSessionView {

    private final ExamSessionRepo examSessionRepo;
    private final LocalizationUtils localizationUtils;
    private final SearchUtils searchUtils;
    private final FinderUtils finderUtils;

    public WrapResponse<?> getDetail(String id){
        ExamSessionEntity exam = examSessionRepo.findById(id).orElse(null);
        boolean isSuccess = true;
        if(exam == null){
            isSuccess = false;
        }
        return WrapResponse.builder()
                .isSuccess(isSuccess)
                .status(isSuccess ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .data(exam)
                .message(isSuccess ? null : localizationUtils.getLocalizedMessage(MessageKeys.EXAM_SESSION_NOT_FOUND))
                .build();
    }

    public WrapResponse<?> getList(SearchListExamSessionRequest request){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        UserEntity crnt = CurrentUser.get();
        if (crnt == null){
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.UNAUTHORIZED).message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)).build();
        }
        if (crnt.getRole() != Role.ADMIN){
            List<String> codes = finderUtils.getUserClassCodes(crnt.getEmail());
            BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
            roleQuery.should(QueryBuilders.matchQuery("createdBy", crnt.getEmail()));
            roleQuery.should(QueryBuilders.termsQuery("classCodes", codes));
            boolQuery.must(roleQuery);
        }
        if (request.getSearchingKeys() != null && !request.getSearchingKeys().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("searchingKeys", request.getSearchingKeys()));
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
                .data(searchUtils.search(searchQuery, ExamSessionDoc.class))
                .status(HttpStatus.OK)
                .build();
    }
}
