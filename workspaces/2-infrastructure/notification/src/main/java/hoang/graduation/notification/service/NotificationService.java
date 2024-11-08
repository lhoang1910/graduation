package hoang.graduation.notification.service;

import com.alpaca.base.BaseObjectLoggable;
import com.alpaca.notification.entity.NotificationView;
import com.alpaca.notification.repo.NotificationViewRepo;
import com.alpaca.notification.request.SearchNotificationRequest;
import com.alpaca.util.PageableUtils;
import lombok.Setter;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static com.alpaca.util.TextUtils.convertStringToSort;

@Service
@Setter
public class NotificationService extends BaseObjectLoggable {
    private final NotificationViewRepo repo;
    private final RestHighLevelClient client;

    public NotificationService(NotificationViewRepo repo, RestHighLevelClient client) {
        this.repo = repo;
        this.client = client;
    }

    public Page<NotificationView> search(String userId, SearchNotificationRequest request) {
        try {
            //sort
            Pageable pageable = PageableUtils.convertPageableAndSort(request.getPageNumber(), request.getPageSize(), request.getSort());
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must().add(QueryBuilders.termQuery("userId", userId));
            if (request.getSystem() != null && !request.getSystem().isEmpty() && !request.getSystem().trim().isEmpty()) {
                boolQuery.must().add(QueryBuilders.termQuery("system", request.getSystem().trim()));
            }
            if (request.getStatus() != null && !request.getStatus().isEmpty() && !request.getStatus().trim().isEmpty()) {
                boolQuery.must().add(QueryBuilders.termQuery("status", request.getStatus().trim()));
            }
            if (request.getKeyword() != null && !request.getKeyword().isEmpty() && !request.getKeyword().trim().isEmpty()) {
                boolQuery.must().add(QueryBuilders.wildcardQuery("contentSort", "*" + convertStringToSort(request.getKeyword().trim()) + "*"));
            }
            if (request.getStartDate() != null && request.getEndDate() != null) {
                boolQuery.must().add(QueryBuilders.rangeQuery("createdDate")
                        .gte(request.getStartDate().getTime())
                        .lte(request.getEndDate().getTime()));
            } else if (request.getStartDate() != null) {
                boolQuery.must().add(QueryBuilders.rangeQuery("createdDate")
                        .gte(request.getStartDate().getTime()));
            } else if (request.getEndDate() != null) {
                boolQuery.must().add(QueryBuilders.rangeQuery("createdDate")
                        .lte(request.getEndDate().getTime()));
            }
            NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(pageable).build();
            Page<NotificationView> page = repo.search(queryBuilder);

            return page;
        } catch (Exception e) {
            return Page.empty();
        }
    }

    public boolean markAsRead(String userId, String id) {
        try {
            //sort
            Optional<NotificationView> optional = repo.findById(id);
            if (optional.isPresent() && optional.get().getUserId().equals(userId)) {
                NotificationView view = optional.get();
                view.setStatus("readed");
                view.setUpdatedDate(new Date());
                repo.save(view);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean markReadAll(String userId) {
        try {
            //sort
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must().add(QueryBuilders.termQuery("userId", userId));
            boolQuery.must().add(QueryBuilders.termQuery("status", "new"));
            UpdateByQueryRequest request =
                    new UpdateByQueryRequest("notification");
            request.setQuery(boolQuery);
            request.setConflicts("proceed");
            request.setScript(new Script(ScriptType.INLINE,
                    "painless", "ctx._source.status = 'readed';ctx._source.updatedDate = params['nowdate'];",
                    Collections.singletonMap("nowdate", new Date().getTime())));
            request.setRefresh(true);
            client
                    .updateByQuery(request, RequestOptions.DEFAULT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String userId, String id) {
        try {
            //sort
            Optional<NotificationView> optional = repo.findById(id);
            if (optional.isPresent() && optional.get().getUserId().equals(userId)) {
                NotificationView view = optional.get();
                repo.delete(view);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAll(String userId) {
        try {
            //sort
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must().add(QueryBuilders.termQuery("userId", userId));
            DeleteByQueryRequest request =
                    new DeleteByQueryRequest("notification");
            request.setQuery(boolQuery);
            request.setRefresh(true);
            client.deleteByQuery(request, RequestOptions.DEFAULT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long totalNew(String userId) {
        return repo.countByUserIdAndStatus(userId, "new");
    }

    public long totalSystemNew(String userId, String system) {
        return repo.countByUserIdAndStatusAndSystem(userId, "new", system);
    }
}
