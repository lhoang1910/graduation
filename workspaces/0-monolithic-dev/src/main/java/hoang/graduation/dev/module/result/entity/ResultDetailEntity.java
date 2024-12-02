package hoang.graduation.dev.module.result.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import hoang.graduation.dev.share.constant.JpaType;
import hoang.graduation.dev.share.model.object.QuestionResultModel;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "student_detail_result")
@Entity
@Builder
@TypeDef(name = JpaType.JSON_BINARY, typeClass = JsonBinaryType.class)
public class ResultDetailEntity {

    @Id
    private String id;

    private String examCode;
    private String examSessionId;
    private String classCode;
    private String examSessionAuthor;
    private String examRandomCode;

    private String studentId;
    private String studentName;
    private String studentCode;
    private String studentEmail;

    private double timeTracking;
    private double score;
    private Date startAt;
    private Date submitAt;

    private int correctAnswer;
    private int correctQuestion;
    private int wrongAnswer;
    private int wrongQuestion;
    private int notAnswer;

    private Integer rank;

    @Column(columnDefinition = "boolean default false")
    private boolean isSubmitted;

    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private List<QuestionResultModel> questionResults;
}
