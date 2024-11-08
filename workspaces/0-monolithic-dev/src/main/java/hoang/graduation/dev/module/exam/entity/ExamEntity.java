package hoang.graduation.dev.module.exam.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import hoang.graduation.share.constant.JpaType;
import hoang.graduation.share.model.object.QuestionModel;
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
@Builder
@Entity
@TypeDef(name = JpaType.JSON_BINARY, typeClass = JsonBinaryType.class)
@Table(name = "exams")
public class ExamEntity {
    @Id
    private String id;
    private String code;
    private String name;
    private String description;

    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private List<QuestionModel> questions;

    private Date createdDate;
    private Date updatedDate;
    private String createdBy;
    private String updatedBy;
}
