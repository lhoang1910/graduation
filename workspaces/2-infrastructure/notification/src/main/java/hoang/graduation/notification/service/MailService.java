package hoang.graduation.notification.service;

import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Setter
public class MailService {
    private final JavaMailSender javaMailSender;

    @Value("${libra.assets.api:http://assets-service}")
    private String assetsUrl;
    @Value("${libra.mail.from-name:Tổng Công ty CP Bảo hiểm Bảo Long}")
    private String fromName;
    @Value("${spring.mail.username}")
    private String fromMail;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Retryable(value = Exception.class)
    public void sendEmail(String subject, String htmlContent, String from, String to, String[] toC, String[] cc, String[] bCC, Map<String, Object> scopes) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        //code phase 2
        if (toC != null && toC.length > 0) {
            helper.setTo(toC);
        } else {
            helper.setTo(to);
        }
        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }
        if(bCC != null){
            helper.setBcc(bCC);
        }

        //code archive/phase1
        helper.setSubject(subject);
        helper.setFrom(fromMail, fromName);
        Multipart multipart = new MimeMultipart();

        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(htmlContent, "text/html; charset=UTF-8"); //5
        multipart.addBodyPart(htmlBodyPart);

        //Code phase 2
        if (scopes != null && scopes.containsKey("logo") && Boolean.TRUE.equals(scopes.getOrDefault("logo", Boolean.FALSE))) {
            MimeBodyPart imgPart = new MimeBodyPart();
            String fileName = "templates/images/baolong-insurance.png";

            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }

            imgPart.setContentID("<logo.png>");
            multipart.addBodyPart(imgPart);
        }

        message.setContent(multipart);
        javaMailSender.send(message);
    }
}
