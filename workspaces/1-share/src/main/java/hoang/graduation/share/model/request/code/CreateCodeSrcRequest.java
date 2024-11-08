package hoang.graduation.share.model.request.code;

import lombok.Data;

@Data
public class CreateCodeSrcRequest {
    String uid;
    String code;
    String filename;
    String language;
}
