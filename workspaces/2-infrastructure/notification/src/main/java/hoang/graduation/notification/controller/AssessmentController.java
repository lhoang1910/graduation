package hoang.graduation.notification.controller;

import com.alpaca.base.BaseObjectLoggable;
import com.alpaca.base.service.response.WrapResponse;
import com.alpaca.notification.service.AssessmentService;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Setter
@RestController
@RequestMapping("/assessment")
public class AssessmentController extends BaseObjectLoggable {
    private final ExecutorService executorService;
    private final AssessmentService service;

    public AssessmentController(ExecutorService executorService, AssessmentService service) {
        this.executorService = executorService;
        this.service = service;
    }

    @RequestMapping(value = "/code-info", method = RequestMethod.GET)
    public CompletableFuture<WrapResponse<Object>> cache(@RequestParam String code) {
        return CompletableFuture.supplyAsync(() -> WrapResponse.ok(service.getAssessment(code)), executorService);
    }

}
