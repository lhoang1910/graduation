package hoang.graduation.dev.module.admin.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "feature_management")
public class FeatureManagementDoc {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Text)
    private String featureCode;
    @Field(type = FieldType.Text)
    private String featureName;
    @Field(type = FieldType.Keyword)
    private String description;
    @Field(type = FieldType.Keyword)
    private boolean isEnable;
}
