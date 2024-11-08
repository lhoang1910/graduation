package hoang.graduation.dev.module.classes.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.component.FinderUtils;
import hoang.graduation.dev.component.SearchUtils;
import hoang.graduation.dev.module.classes.doc.ClassDoc;
import hoang.graduation.dev.module.classes.entity.ClassEntity;
import hoang.graduation.dev.module.classes.repo.ClassESRepo;
import hoang.graduation.dev.module.classes.repo.ClassRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.page.BaseSort;
import hoang.graduation.dev.page.SearchListClassRequest;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ClassView {
    private final ClassRepo classRepo;
    private final ClassESRepo classESRepo;
    private final SearchUtils searchUtils;
    private final FinderUtils finderUtils;

    public WrapResponse<?> getDetail(String id) {
        ClassEntity clss = classRepo.findById(id).orElse(null);
        boolean isSuccess = clss != null;
        return WrapResponse.builder()
                .isSuccess(isSuccess)
                .status(isSuccess ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .data(clss)
                .build();
    }

    public WrapResponse<Object> searchClassList(SearchListClassRequest request) {
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (crnt.getRole() != Role.ADMIN){
            List<String> codes = finderUtils.getUserClassCodes(crnt.getEmail());
            BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
            roleQuery.should(QueryBuilders.matchQuery("createdBy", crnt.getEmail()));
            roleQuery.should(QueryBuilders.termsQuery("classCode", codes));
            boolQuery.must(roleQuery);
        }

        if (request.getSearchingKeys() != null && !request.getSearchingKeys().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("searchingKeys", request.getSearchingKeys()));
        }

        if (request.getLimitSlot() != null) {
            boolQuery.filter(QueryBuilders.termQuery("limitSlot", request.getLimitSlot()));
        }
        if (request.getPracticeAmount() != null) {
            boolQuery.filter(QueryBuilders.termQuery("practiceAmount", request.getPracticeAmount()));
        }
        if (request.getExamineAmount() != null) {
            boolQuery.filter(QueryBuilders.termQuery("examineAmount", request.getExamineAmount()));
        }

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
                .data(searchUtils.search(searchQuery, ClassDoc.class))
                .status(HttpStatus.OK)
                .build();
    }
}
