package hoang.graduation.dev.module.classes.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "class_list")
public class ClassDoc {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String classCode;
    @Field(type = FieldType.Keyword)
    private String className;

    @Field(type = FieldType.Keyword)
    private int limitSlot;
    @Field(type = FieldType.Keyword)
    private int practiceAmount;
    @Field(type = FieldType.Keyword)
    private int examineAmount;
    @Field(type = FieldType.Keyword)
    private int participationAmount;

    @Field(type = FieldType.Keyword)
    private String createdByName;
    @Field(type = FieldType.Keyword)
    private String createdByEmail;

    @Field(type = FieldType.Keyword)
    private List<String> userEmails;

    private Date createAt;
    private Date updateAt;

    @Field(type = FieldType.Text)
    private String searchingKeys;

    public void formatSearchingKeys() {
        String noSpaceCode = removeAccentsAndSpaces(classCode);
        String noSpaceClassName = removeAccentsAndSpaces(className);
        String noSpaceCreatedBy = removeAccentsAndSpaces(createdByName);

        searchingKeys = String.format(
                "|%s|%s|%s|%s|%s|%s|",
                classCode != null ? classCode : "",
                noSpaceCode,
                className != null ? className : "",
                noSpaceClassName,
                createAt != null ? createAt : "",
                noSpaceCreatedBy
        ).replaceAll("\\|+$", "");
    }

    private String removeAccentsAndSpaces(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("\\s+", "");
    }
}
