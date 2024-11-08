package hoang.graduation.share.model.request.code;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RunCodeRequest {
    String language; String code; String input;
}
