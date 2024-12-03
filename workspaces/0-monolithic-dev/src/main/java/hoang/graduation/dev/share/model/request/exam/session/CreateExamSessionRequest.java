package hoang.graduation.dev.share.model.request.exam.session;

import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateExamSessionRequest {
    private String examId;
    private String examCode;
    private String name;
    private String description;
    @Builder.Default
    private Boolean isOpen = false;       // Nếu public thì không cần set classCodes
    private List<String> classCodes;
    // setting
    private int time;
    private Date effectiveDate;
    private Date expirationDate;
    private int pointType;

    private int questionAmount;     // số lượng câu hỏi trong đề
    private int randomAmount;       // Mã đề
    private int limitation;         // Giới hạn lam
}