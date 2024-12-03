package hoang.graduation.dev.share.model.object;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class AnswerResultModel {
    private String id;
    private String questionCode;
    private String answer;
    private String attachmentPath;
    private boolean isCorrect;
    private boolean isChosen;
}
