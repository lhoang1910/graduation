package hoang.graduation.dev.component;

import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchUtils {

    private final ElasticsearchRestTemplate restTemplate;

    public SearchUtils(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <D> List<D> search(Query query, Class<D> clazz) {

        SearchHits<D> searchHits = restTemplate.search(query, clazz);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
