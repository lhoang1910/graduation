package hoang.graduation.notification.action;

import freemarker.template.Configuration;
import freemarker.template.Template;
import hoang.graduation.notification.entity.EmailLogView;
import hoang.graduation.notification.repo.EmailLogViewRepo;
import hoang.graduation.notification.service.MailService;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

@Setter
@Component
public class SendEmailNotificationAction {
    private final MailService mailService;
    private final Configuration freemarkerConfig;
    private final ApplicationContext context;
    private final EmailLogViewRepo repo;
    @Value("${alpaca.send-notification-default.active:false}")
    private Boolean activeDefault;
    @Value("${alpaca.send-notification-default.to-mail:email.libra.stagging@alpaca.vn}")
    private String mailDefault;

    public SendEmailNotificationAction(Configuration freemarkerConfig,
                                       MailService mailService,
                                       ApplicationContext context,
                                       EmailLogViewRepo repo) {
        this.mailService = mailService;
        this.freemarkerConfig = freemarkerConfig;
        this.context = context;
        this.repo = repo;
    }

    @RabbitListener(queues = "graduation-send-notification", concurrency = "2")
    public void runAction(NotificationModel model) {
        if (activeDefault) {
            model.setTo(mailDefault);
            model.setCc(null);
        }
        String[] cc = model.getCc() == null ? new String[]{""} : model.getCc();
        EmailLogView view = EmailLogView.builder()
                .from(model.getFrom())
                .to(model.getTo())
                .toC(model.getToC())
                .cc(cc)
                .bCC(model.getBCC())
                .subject(model.getSubject())
                .template(model.getTemplate()).build();
        try {
            //code phase 2
            String htmlContent;
            if (model.getExternalTemplate() != null && model.getExternalTemplate()) {
                htmlContent = model.getBody();
            } else {
                //code archive/phase1
                Resource resource = context.getResource("classpath:templates/mail/" + model.getTemplate() + ".ftlh");
                if (!resource.exists()) {
                    view.setStatus("error");
                    view.setError("template not found");
                    repo.save(view);
                    return;
                }
                Writer output = new StringWriter();
                Template template = new Template(model.getTemplate(), new InputStreamReader(resource.getInputStream()), freemarkerConfig);
                template.process(model.getScopes(), output);
                htmlContent = output.toString();
            }
            view.setContent(htmlContent);
            mailService.sendEmail(model.getSubject(), htmlContent, model.getFrom(), model.getTo(), model.getToC(), model.getCc(), model.getBCC(), model.getScopes());
        } catch (Exception e) {
            try {
                view.setStatus("error");
                view.setError(e.toString());
                repo.save(view);
            } catch (Exception e1) {

            }
        }
    }
}
