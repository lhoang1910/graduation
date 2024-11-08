package hoang.graduation.dev.module.result.controller;

import hoang.graduation.dev.module.result.ResultView;
import hoang.graduation.dev.module.result.service.ResultService;
import hoang.graduation.dev.page.SearchListResultRequest;
import hoang.graduation.share.model.request.result.SubmitResultRequest;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/result")
@RestController
public class ResultController {
    private final ResultService resultService;
    private final ResultView resultView;

    @PostMapping("/{examSessionId}/start")
    public WrapResponse<?> start(@PathVariable String examSessionId){
        return resultService.startExam(examSessionId);
    }

    @PostMapping("/{resultId}/submit")
    public WrapResponse<?> submit(@PathVariable String resultId, @RequestBody SubmitResultRequest request){
        return resultService.submitExam(resultId, request);
    }

    @PostMapping("/{resultId}")
    public WrapResponse<?> getDetail(@PathVariable String resultId){
        return resultView.getDetail(resultId);
    }

    @PostMapping("/list")
    public WrapResponse<?> searchList(@RequestBody SearchListResultRequest request){
        return resultView.getListResult(request);
    }
}
