package hoang.graduation.dev.share.model.request.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnAssignClassRequest {
    String examId; String classCode;
}
