package hoang.graduation.notification.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "notification")
public class NotificationView implements Serializable {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String userId;
    @Field(type = FieldType.Keyword)
    private String userName;
    @Field(type = FieldType.Keyword)
    private String icon;
    @Field(type = FieldType.Keyword)
    private String status;
    @Field(type = FieldType.Keyword)
    private String action;
    @Field(type = FieldType.Keyword)
    private String system;
    private String content;
    @Field(type = FieldType.Keyword)
    private String contentSort;
    @DynamicMapping(DynamicMappingValue.True)
    @Field(type = FieldType.Nested)
    private Map<String, Object> target;
    private Date createdDate;
    private Date updatedDate;
}
