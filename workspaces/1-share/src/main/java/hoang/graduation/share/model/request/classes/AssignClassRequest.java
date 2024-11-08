package hoang.graduation.share.model.request.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignClassRequest {
    private String classCode;
    private String examId;
    private boolean isPractice;
    private Date effectiveAt;
    private Date expirationAt;
    private String maxTurn;
    private String description;
}

