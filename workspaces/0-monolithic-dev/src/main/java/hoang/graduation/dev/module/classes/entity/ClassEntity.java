package hoang.graduation.dev.module.classes.entity;

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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "classes")
@TypeDef(name = JpaType.JSON_BINARY, typeClass = JsonBinaryType.class)
public class  ClassEntity {
    @Id
    private String id;
    private String classCode;
    private String className;

    private int limitSlot;

    @Column(columnDefinition = "int default 0")
    private int practiceAmount;
    @Column(columnDefinition = "int default 0")
    private int examineAmount;
    private String createdByName;
    private String createdByEmail;

    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private List<String> userEmails;

    private Date createAt;
    private Date updateAt;
}
