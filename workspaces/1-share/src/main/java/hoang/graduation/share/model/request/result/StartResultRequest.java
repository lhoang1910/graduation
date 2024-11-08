package hoang.graduation.share.model.request.result;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StartResultRequest {
    private Date startAt;
}
