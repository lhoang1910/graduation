package hoang.graduation.notification.action;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationModel { private static final long serialVersionUID = 1152811004560597017L;
    private String template;
    private String from;
    private String to;
    private String[] toC;
    private String[] bCC;
    private String[] cc;
    private String subject;
    private Map<String, Object> scopes;
    private Boolean externalTemplate;
    private String body;

}
