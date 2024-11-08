package hoang.graduation.dev.module.premium.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.text.Normalizer;

@Document(indexName = "premium")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PremiumPackageDoc {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String code;
    @Field(type = FieldType.Keyword)
    private Long price;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword)
    private String description;
    @Field(type = FieldType.Keyword)
    private Long limitClassSlot;
    @Field(type = FieldType.Keyword)
    private Long limitPracticeTurn;

    @Field(type = FieldType.Text)
    private String searchingKeys;

    public void formatSearchingKeys() {
        String noSpaceCode = removeAccentsAndSpaces(code);
        String noSpaceName = removeAccentsAndSpaces(name);

        searchingKeys = String.format(
                "|%s|%s|%s|%s|",
                code != null ? code : "",
                noSpaceCode,
                name != null ? name : "",
                noSpaceName
        ).replaceAll("\\|+$", "");
    }

    private String removeAccentsAndSpaces(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("\\s+", "");
    }
}
