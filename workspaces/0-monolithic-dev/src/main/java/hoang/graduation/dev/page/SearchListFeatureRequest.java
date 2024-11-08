package hoang.graduation.dev.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchListFeatureRequest extends BasePage{
    private String featureCode;
    private String featureName;
}
