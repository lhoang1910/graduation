package hoang.graduation.dev.share.model.request.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeatureRequest {
    private String featureCode;
    private String featureName;
    private String description;
    @JsonProperty("isEnable")
    private boolean isEnable;
}
