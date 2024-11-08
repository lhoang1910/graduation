package hoang.graduation.notification.config;

import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreeMakerConfig {
    public freemarker.template.Configuration freemakerConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
//        cfg.setClassForTemplateLoading(this.getClass(), "templates/");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);

        return cfg;
    }
}
