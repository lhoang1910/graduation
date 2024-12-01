package hoang.graduation.dev.module.result.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(indexName = "exam_session_result")
public class ResultDetailDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String examSessionId;
    @Field(type = FieldType.Keyword)
    private String examSessionName;
    @Field(type = FieldType.Keyword)
    private String examSessionAuthor;

    @Field(type = FieldType.Keyword)
    private String studentCode;
    @Field(type = FieldType.Keyword)
    private String studentName;

    @Field(type = FieldType.Keyword)
    private double timeTracking;
    @Field(type = FieldType.Keyword)
    private double score;
    @Field(type = FieldType.Keyword)
    private Date startAt;
    @Field(type = FieldType.Keyword)
    private Date submitAt;
    @Field(type = FieldType.Keyword)
    private int correctAnswer;
    @Field(type = FieldType.Keyword)
    private int correctQuestion;
    @Field(type = FieldType.Keyword)
    private int wrongAnswer;
    @Field(type = FieldType.Keyword)
    private int wrongQuestion;
    @Field(type = FieldType.Keyword)
    private int notAnswer;
    @Field(type = FieldType.Keyword)
    private Integer rank;

    @Field(type = FieldType.Text)
    private String searchingKeys;
}
