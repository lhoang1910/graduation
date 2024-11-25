package hoang.graduation.share.model.object;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuestionModel {
    private String id;
    private String code;
    private String attachmentPath;
    private String question;
    private int type;
    private List<AnswerModel> answers;
    private String explain;
    private String explainFilePath;
}
