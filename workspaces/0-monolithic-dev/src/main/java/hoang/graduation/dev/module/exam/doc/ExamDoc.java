package hoang.graduation.dev.module.exam.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.text.Normalizer;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(indexName = "exam_list")
public class ExamDoc {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String code;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword)
    private String description;
    @Field(type = FieldType.Keyword)
    private int questionAmount;

    private Date createdDate;
    private Date updatedDate;
    private String createdBy;
    private String updatedBy;

    @Field(type = FieldType.Text)
    private String searchingKeys;

    public void formatSearchingKeys() {
        String noSpaceCode = removeAccentsAndSpaces(code);
        String noSpaceName = removeAccentsAndSpaces(name);
        String noSpaceCreatedBy = removeAccentsAndSpaces(createdBy);

        searchingKeys = String.format(
                "|%s|%s|%s|%s|%s|%s|",
                code,
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
