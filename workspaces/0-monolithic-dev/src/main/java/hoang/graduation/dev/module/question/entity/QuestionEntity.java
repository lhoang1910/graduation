package hoang.graduation.dev.module.question.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import hoang.graduation.share.model.object.AnswerModel;
import hoang.graduation.share.constant.JpaType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "question")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@TypeDef(name = JpaType.JSON_BINARY, typeClass = JsonBinaryType.class)
public class QuestionEntity {
    @Id
    private String id;
    private String code;
    private String attachmentPath;
    private String question;
    private int type;
    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private List<AnswerModel> answers;

    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
