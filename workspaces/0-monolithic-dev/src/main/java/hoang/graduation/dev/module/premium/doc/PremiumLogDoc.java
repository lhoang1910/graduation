package hoang.graduation.dev.module.premium.doc;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.text.Normalizer;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(indexName = "premium_log")
public class PremiumLogDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Keyword)
    private String premiumCode;
    @Field(type = FieldType.Keyword)
    private String premiumName;
    @Field(type = FieldType.Keyword)
    private int monthAmount;
    @Field(type = FieldType.Keyword)
    private Long totalAmount;
    @Field(type = FieldType.Keyword)
    private String userEmail;
    @Field(type = FieldType.Keyword)
    private String buyerName;
    @Field(type = FieldType.Keyword)
    private Long limitClassSlot;
    @Field(type = FieldType.Keyword)
    private Long limitPracticeTurn;

    private Date boughtDate;
    private Date expiredDate;

    @Field(type = FieldType.Keyword, name = "isActive")
    private boolean isActive;

    @Field(type = FieldType.Text)
    private String searchingKeys;

    public String formatSearchingKeys() {
        String noSpaceCode = removeAccentsAndSpaces(premiumCode);
        String noSpaceName = removeAccentsAndSpaces(premiumName);
        String noSpaceBuyerName = removeAccentsAndSpaces(buyerName);
        String noSpaceUserEMail = removeAccentsAndSpaces(userEmail);

        return String.format(
                "|%s|%s|%s|%s|%s|%s|%s|%s|",
                premiumCode != null ? premiumCode : "",
                noSpaceCode,
                premiumName != null ? premiumName : "",
                noSpaceName,
                buyerName != null ? buyerName : "",
                noSpaceBuyerName,
                userEmail != null ? userEmail : "",
                noSpaceUserEMail
        ).replaceAll("\\|+$", "");
    }

    private String removeAccentsAndSpaces(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("\\s+", "");
    }
}
