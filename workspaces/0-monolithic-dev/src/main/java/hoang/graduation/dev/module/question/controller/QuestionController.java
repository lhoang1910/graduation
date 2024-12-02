package hoang.graduation.dev.module.question.controller;

import hoang.graduation.dev.module.question.service.DetectQuestion;
import hoang.graduation.dev.share.model.object.QuestionModel;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final DetectQuestion detectQuestion;

    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WrapResponse<List<QuestionModel>> detect(@RequestParam("fileType") int fileType, @RequestParam("file") MultipartFile file){
        return detectQuestion.detectQuestionFromFile(fileType, file);
    }
}
