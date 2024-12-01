package hoang.graduation.share.model.object;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuestionResultModel {
    private String id;
    private String code;
    private String question;
    private List<AnswerResultModel> answers;
    private String explain;
}
