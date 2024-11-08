package hoang.graduation.dev.module.exam.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@TypeDef(name = JpaType.JSON_BINARY, typeClass = JsonBinaryType.class)
@Table(name = "exam_session")
public class ExamSessionEntity {

    @Id
    private String id;

    private String examCode;
    private String name;
    private String description;

    private boolean isPublic;       // Nếu public thì không cần set classCodes

    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private List<String> classCodes;

    // setting
    private int time;

    private Date effectiveDate;
    private Date expirationDate;

    private int questionAmount;     // số lượng câu hỏi trong đề
    private int randomAmount;       // Mã đề
    private int limitation;         // Số lượt làm tối đa
    private int pointType;

    private String createdBy;
    private String updatedBy;
    private Date createdDate;
    private Date updatedDate;
}
