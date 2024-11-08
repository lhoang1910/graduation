package hoang.graduation.notification.controller;

import com.alpaca.auth.store.model.UserDetailModel;
import com.alpaca.base.BaseObjectLoggable;
import com.alpaca.base.service.response.WrapResponse;
import com.alpaca.notification.api.SmsApi;
import com.alpaca.notification.common.RabbitMQQueue;
import com.alpaca.notification.entity.NotificationView;
import com.alpaca.notification.entity.SmsLogView;
import com.alpaca.notification.model.NotificationModel;
import com.alpaca.notification.model.SmsNotificationModel;
import com.alpaca.notification.model.SystemNotificationModel;
import com.alpaca.notification.request.SearchNotificationRequest;
import com.alpaca.notification.request.SendSmsRequest;
import com.alpaca.notification.response.SendSmsResponse;
import com.alpaca.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Setter
@RestController
@RequestMapping("/notify")
public class NotificationController extends BaseObjectLoggable {
    private final ExecutorService executorService;
    private final NotificationService service;
    private final RabbitTemplate rabbitTemplate;
    private final Configuration freemakerConfig;
    private final SmsApi api;
    @Value("${alpaca.sms.from:BAOLONG}")
    private String from;
    @Value("${alpaca.sms.token:Basic YmFvbG9uZzI6QVJBSG1iakU=}")
    private String token;
    private final ObjectMapper mapper;
    @Value("${alpaca.send-notification-default.active:false}")
    private Boolean activeDefault;
    @Value("${alpaca.send-notification-default.to-sms:0586072996}")
    private String smsDefault;

    public NotificationController(ExecutorService executorService, NotificationService service, RabbitTemplate rabbitTemplate, Configuration freemakerConfig, SmsApi api, ObjectMapper mapper) {
        this.executorService = executorService;
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
        this.freemakerConfig = freemakerConfig;
        this.api = api;
        this.mapper = mapper;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Page<NotificationView>>> search(final Principal principal, @RequestBody SearchNotificationRequest request) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.search(userDetailModel.getUserId(), request)), executorService);
    }

    @RequestMapping(value = "/mark-read/{id}", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> markRead(final Principal principal, @PathVariable String id) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.markAsRead(userDetailModel.getUserId(), id)), executorService);
    }

    @RequestMapping(value = "/mark-all-read", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> markAllRead(final Principal principal) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.markReadAll(userDetailModel.getUserId())), executorService);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> delete(final Principal principal, @PathVariable String id) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.delete(userDetailModel.getUserId(), id)), executorService);
    }

    @RequestMapping(value = "/delete-all", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> deleteAll(final Principal principal) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.deleteAll(userDetailModel.getUserId())), executorService);
    }

    @RequestMapping(value = "/total-new", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Long>> totalNew(final Principal principal) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.totalNew(userDetailModel.getUserId())), executorService);
    }

    @RequestMapping(value = "/{system}/total-new", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Long>> totalSystemNew(final Principal principal, @PathVariable String system) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.totalSystemNew(userDetailModel.getUserId(), system)), executorService);
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> test(final Principal principal, @RequestBody SystemNotificationModel model) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        UserDetailModel userDetailModel = (UserDetailModel) authentication.getUserAuthentication().getPrincipal();
        model.setUserId(userDetailModel.getUserId());
        rabbitTemplate.convertAndSend(RabbitMQQueue.QUEUE_SEND_SYSTEM_NOTIFICATION, model);
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(true), executorService);
    }

    @RequestMapping(value = "/test-mail", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> testMail(@RequestBody NotificationModel model) {
        rabbitTemplate.convertAndSend(RabbitMQQueue.QUEUE_SEND_NOTIFICATION, model);
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(true), executorService);
    }

    @RequestMapping(value = "/test-sms", method = RequestMethod.POST)
    public CompletableFuture<WrapResponse<Boolean>> testSms(@RequestBody SmsNotificationModel model) {
        rabbitTemplate.convertAndSend(RabbitMQQueue.QUEUE_SEND_SMS_NOTIFICATION, model);
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(true), executorService);
    }

    public void runAction(SmsNotificationModel model) {
        if (activeDefault) {
            model.setTo(smsDefault);
        }
        SmsLogView view = SmsLogView.builder()
                .from(from)
                .to(model.getTo())
                .template(model.getTemplate()).build();
        try {

            String to = model.getTo();
            if (to.startsWith("0")) {
                to = "84" + to.substring(1);
            }
            String defTemplate = model.getDefTemplate();
            Template template = new Template(model.getTemplate(), new StringReader(defTemplate), freemakerConfig);
            Writer output = new StringWriter();
//            Template template = new Template(model.getTemplate(), new InputStreamReader(resource.getInputStream()), freemakerConfig);
            template.process(model.getScopes(), output);
            final String htmlContent = output.toString();
            view.setContent(htmlContent);

            SendSmsResponse response = api.sendSms(token, SendSmsRequest.builder().from(from).to(to).text(htmlContent).build());

            logger.error(">>>>>>>>>>>>>>>>>>send sms success {}", mapper.writeValueAsString(response));
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>> loi gui sms ", e);
            view.setStatus("error");
            view.setResponse(e.toString());
        }
    }
}
