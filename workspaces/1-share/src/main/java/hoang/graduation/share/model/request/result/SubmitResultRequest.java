package hoang.graduation.share.model.request.result;

import hoang.graduation.share.model.object.QuestionResultModel;
import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubmitResultRequest {
    private List<QuestionResultModel> questionResults;
}
