package hoang.graduation.share.model.object;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class AnswerModel {
    private String id;
    private String index;
    private String questionCode;
    private String answer;
    private String attachmentPath;
    private boolean isCorrect;
}
