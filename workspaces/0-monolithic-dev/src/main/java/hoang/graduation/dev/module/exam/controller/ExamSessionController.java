package hoang.graduation.dev.module.exam.controller;

import hoang.graduation.dev.module.exam.service.ExamSessionService;
import hoang.graduation.dev.module.exam.view.ExamSessionView;
import hoang.graduation.dev.page.SearchListExamSessionRequest;
import hoang.graduation.share.model.request.exam.session.CreateExamSessionRequest;
import hoang.graduation.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/exam-session")
public class ExamSessionController {

    private final ExamSessionService examSessionService;
    private final ExamSessionView examSessionView;

    @PostMapping("/create")
    public WrapResponse<?> createExamSession(@RequestBody CreateExamSessionRequest request) {
        return examSessionService.createExamSession(request);
    }

    @PostMapping("/{id}/update")
    public WrapResponse<?> updateExam(@PathVariable String id, @RequestBody CreateExamSessionRequest request) {
        return examSessionService.updateExamSession(id, request);
    }

    @PostMapping("/{id}/delete")
    public WrapResponse<?> deleteExam(@PathVariable String id) {
        return examSessionService.deleteExamSession(id);
    }

    @GetMapping("/{id}")
    public WrapResponse<?> getExam(@PathVariable String id) {
        return examSessionView.getDetail(id);
    }

    @PostMapping("/list")
    public WrapResponse<?> searchList(@RequestBody SearchListExamSessionRequest request) {
        return examSessionView.getList(request);
    }
}
