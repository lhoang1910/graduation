package hoang.graduation.dev.module.exam.doc;

import hoang.graduation.dev.share.constant.JpaType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Id;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(indexName = "exam_session_list")
public class ExamSessionDoc {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String examCode;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword)
    private String description;
    @Field(type = FieldType.Keyword)
    private boolean isPublic;       // Nếu public thì không cần set classCodes

    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private List<String> classCodes;

    // setting
    @Field(type = FieldType.Keyword)
    private int time;
    @Field(type = FieldType.Keyword)
    private Date effectiveDate;
    @Field(type = FieldType.Keyword)
    private Date expirationDate;
    @Field(type = FieldType.Keyword)
    private int questionAmount;
    // số lượng câu hỏi trong đề
    @Field(type = FieldType.Keyword)
    private int randomAmount;       // Mã đề
    @Field(type = FieldType.Keyword)
    private int limitation;         // Số lượt làm tối đa

    private String createdBy;
    private String updatedBy;
    private Date createdDate;
    private Date updatedDate;

    @Field(type = FieldType.Text)
    private String searchingKeys;

    public void formatSearchingKeys() {
        String noSpaceCode = removeAccentsAndSpaces(examCode);
        String noSpaceName = removeAccentsAndSpaces(name);
        String noSpaceCreatedBy = removeAccentsAndSpaces(createdBy);

        searchingKeys = String.format(
                "|%s|%s|%s|%s|%s|%s|",
                examCode,
                noSpaceCode,
                name,
                noSpaceName,
                createdBy,
                noSpaceCreatedBy
        ).replaceAll("\\|+$", "");
    }

    private String removeAccentsAndSpaces(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("\\s+", "").toLowerCase();
    }
}
