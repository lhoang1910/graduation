package hoang.graduation.dev.share.model.request.Question;

import hoang.graduation.dev.share.model.object.AnswerModel;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateQuestionRequest {
    private String id;
    private String attachmentPath;
    private String question;
    private int type;
    private List<AnswerModel> answers;
    private String updatedBy;
}
