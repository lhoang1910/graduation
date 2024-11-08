package hoang.graduation.notification.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "email-log")
public class EmailLogView {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    private String subject;
    @Field(type = FieldType.Keyword)
    private String template;
    @Field(type = FieldType.Keyword)
    private String from;
    private String to;
    private String[] toC;
    private String[] cc;
    private String[] bCC;
    @Field(type = FieldType.Keyword)
    private String status;
    private String content;
    private String error;
}
