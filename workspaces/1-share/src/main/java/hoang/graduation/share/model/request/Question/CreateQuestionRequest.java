package hoang.graduation.share.model.request.Question;

import hoang.graduation.share.model.object.AnswerModel;
import lombok.*;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateQuestionRequest {
    private String attachmentPath;
    private String question;
    private int type;
    private List<AnswerModel> answers;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
