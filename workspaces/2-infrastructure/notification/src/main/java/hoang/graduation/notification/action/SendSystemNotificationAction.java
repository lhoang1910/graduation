package hoang.graduation.notification.action;

import com.alpaca.base.BaseObjectLoggable;
import com.alpaca.notification.common.RabbitMQQueue;
import com.alpaca.notification.entity.NotificationView;
import com.alpaca.notification.model.StompNotificationModel;
import com.alpaca.notification.model.SystemNotificationModel;
import com.alpaca.notification.repo.NotificationViewRepo;
import com.alpaca.util.TextUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import static com.alpaca.util.TextUtils.convertStringToSort;

@Setter
@Component
public class SendSystemNotificationAction extends BaseObjectLoggable {
    private final Configuration freemakerConfig;
    private final ApplicationContext context;
    private final NotificationViewRepo repo;
    private final RabbitTemplate rabbitTemplate;

    public SendSystemNotificationAction(Configuration freemakerConfig, ApplicationContext context, NotificationViewRepo repo, RabbitTemplate rabbitTemplate) {
        this.freemakerConfig = freemakerConfig;
        this.context = context;
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQQueue.QUEUE_SEND_SYSTEM_NOTIFICATION,concurrency = "2")
    public void runAction(SystemNotificationModel model) {
        try {
            logger.info("============== SystemNotification userId: " + model.getUserId() + ", content: " + model.getDefTemplate());
            String tplPath = model.getSystem() + "/" + TextUtils.convert2TemplateName(model.getAction());
            Resource resource = context.getResource("classpath:templates/" + tplPath + ".ftlh");
            String defTemplate = model.getDefTemplate();
            Template template = new Template(tplPath, resource.exists() ? new InputStreamReader(resource.getInputStream()) : new StringReader(defTemplate), freemakerConfig);
            Writer output = new StringWriter();
            template.process(model.getData(), output);
            NotificationView view = NotificationView.builder().status("new").action(model.getAction())
                    .userId(model.getUserId()).userName(model.getUserName()).icon(model.getIcon())
                    .system(model.getSystem())
                    .content(output.toString())
                    .contentSort(convertStringToSort(output.toString()))
                    .createdDate(new Date()).updatedDate(new Date())
                    .target(model.getTarget()).build();

            repo.save(view);
            rabbitTemplate.convertAndSend(RabbitMQQueue.QUEUE_SEND_STOMP_NOTIFICATION, StompNotificationModel.builder()
                    .topic("/topic/notify/user/"+model.getUserId())
                    .data(view).build());
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>> loi gui notification ", e);
        }
    }

}
