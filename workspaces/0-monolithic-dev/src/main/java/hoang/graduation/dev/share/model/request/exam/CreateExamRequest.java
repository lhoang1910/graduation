package hoang.graduation.dev.share.model.request.exam;

import hoang.graduation.dev.share.model.object.QuestionModel;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateExamRequest {
    private String name;
    private String description;
    private List<QuestionModel> questions;
}
