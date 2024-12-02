package hoang.graduation.dev.module.exam.controller;

import hoang.graduation.dev.module.exam.service.ExamService;
import hoang.graduation.dev.module.exam.view.ExamView;
import hoang.graduation.dev.page.SearchListExamRequest;
import hoang.graduation.dev.share.model.request.exam.CreateExamRequest;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exam")
public class ExamController {
    private final ExamService examService;
    private final ExamView examView;

    @PostMapping("/create")
    public WrapResponse<?> createExam(@RequestBody CreateExamRequest request) {
        return examService.createExam(request);
    }

    @PostMapping("/{id}/update")
    public WrapResponse<?> updateExam(@PathVariable String id, @RequestBody CreateExamRequest request) {
        return examService.updateExam(id, request);
    }

    @PostMapping("/{id}/delete")
    public WrapResponse<?> deleteExam(@PathVariable String id, @RequestBody boolean isDeleteAll) {
        return examService.deleteExam(id, isDeleteAll);
    }

    @GetMapping("/{id}")
    public WrapResponse<?> getExam(@PathVariable String id) {
        return examView.getDetail(id);
    }

    @PostMapping("/list")
    public WrapResponse<?> searchList(@RequestBody SearchListExamRequest request) {
        return examView.getList(request);
    }
}
