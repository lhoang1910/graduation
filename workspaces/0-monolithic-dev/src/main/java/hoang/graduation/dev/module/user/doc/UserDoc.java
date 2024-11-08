package hoang.graduation.dev.module.user.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.text.Normalizer;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user_list")
public class UserDoc {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String code;
    @Field(type = FieldType.Keyword)
    private String fullName;
    @Field(type = FieldType.Keyword)
    private String phoneNumber;
    @Field(type = FieldType.Keyword)
    private String email;
    @Field(type = FieldType.Keyword)
    private int gender;
    @Field(type = FieldType.Keyword)
    private int role;
    @Field(type = FieldType.Keyword)
    private boolean isActive;

    private Date birthDay;
    private Date createdAt;
    private Date updatedAt;
    private Date latestActive;

    // searching key
    @Field(type = FieldType.Text)
    private String searchingKeys;

    public void formatSearchingKeys() {
        String noSpaceCode = removeAccentsAndSpaces(code);
        String noSpaceFullName = removeAccentsAndSpaces(fullName);
        String noSpacePhoneNumber = removeAccentsAndSpaces(phoneNumber);
        String noSpaceEmail = removeAccentsAndSpaces(email);

        searchingKeys = String.format(
                "|%s|%s|%s|%s|%s|%s|%s|%s|",
                code != null ? code : "",
                noSpaceCode,
                fullName != null ? fullName : "",
                noSpaceFullName,
                phoneNumber != null ? phoneNumber : "",
                noSpacePhoneNumber,
                email != null ? email : "",
                noSpaceEmail
        ).replaceAll("\\|+$", "");
    }

    private String removeAccentsAndSpaces(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("\\s+", "");
    }
}
