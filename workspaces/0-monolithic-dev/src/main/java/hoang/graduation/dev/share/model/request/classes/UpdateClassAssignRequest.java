package hoang.graduation.dev.share.model.request.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateClassAssignRequest {
    private boolean isPractice;
    private Date effectiveAt;
    private Date expirationAt;
    private int maxTurn;
    private String description;
}
