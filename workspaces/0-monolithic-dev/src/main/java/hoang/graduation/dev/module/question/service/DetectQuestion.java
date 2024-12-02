package hoang.graduation.dev.module.question.service;

import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.question.constant.FileType;
import hoang.graduation.dev.share.model.object.AnswerModel;
import hoang.graduation.dev.share.model.object.QuestionModel;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetectQuestion {

    private final LocalizationUtils localizationUtils;

    public WrapResponse<List<QuestionModel>> detectQuestionFromFile(int fileType, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new WrapResponse<>(false, HttpStatus.BAD_REQUEST, localizationUtils.getLocalizedMessage(MessageKeys.FILE_NOT_FOUND));
            }
            String text = null;
            if (fileType == FileType.DOCX){
                text = extractTextFromDOCX(file);
            } else if (fileType == FileType.PDF){
                text = extractTextFromPDF(file);
            }
            if (StringUtils.isBlank(text)) {
                return new WrapResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, localizationUtils.getLocalizedMessage(MessageKeys.EXTRACT_ERROR));
            }
            List<QuestionModel> questionModel = parseQuestionsFromText(text);

            return new WrapResponse<>(true, HttpStatus.OK, localizationUtils.getLocalizedMessage(MessageKeys.EXTRACT_QUIZ_SUCCESSFULLY), questionModel);
        } catch (Exception e) {
            return new WrapResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR, localizationUtils.getLocalizedMessage(MessageKeys.EXTRACT_ERROR));
        }
    }

    private String extractTextFromDOCX(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    text.append(run.text());
                }
                text.append("\n");
            }
            return text.toString();
        }
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private List<QuestionModel> parseQuestionsFromText(String text) {
        Pattern questionPattern = Pattern.compile("(Câu|Bài|Question)\\s*\\d+\\.\\s*(.*)|\\d+\\.\\s*(.*)", Pattern.MULTILINE);
        Pattern answerPattern = Pattern.compile("([A-D])\\.\\s*(.*)", Pattern.MULTILINE);

        Matcher questionMatcher = questionPattern.matcher(text);
        Matcher answerMatcher = answerPattern.matcher(text);

        List<QuestionModel> questions = new ArrayList<>();
        QuestionModel currentQuestion = null;

        // Tìm tất cả câu hỏi trước
        List<Integer> questionPositions = new ArrayList<>();
        while (questionMatcher.find()) {
            questionPositions.add(questionMatcher.start()); // Lưu vị trí bắt đầu của câu hỏi
        }

        // Khởi động lại matcher để tìm lại từ đầu
        questionMatcher.reset();

        // Duyệt qua từng câu hỏi
        while (questionMatcher.find()) {
            if (currentQuestion != null) {
                questions.add(currentQuestion);
            }

            String questionText = questionMatcher.group(2) != null ? questionMatcher.group(2) : questionMatcher.group(3);
            currentQuestion = new QuestionModel();
            currentQuestion.setQuestion(questionText);
            currentQuestion.setAnswers(new ArrayList<>());

            // Xác định vị trí của câu hỏi tiếp theo (nếu có)
            int currentQuestionEnd = questionMatcher.end();
            int nextQuestionStart = text.length();
            if (questionMatcher.find()) {
                nextQuestionStart = questionMatcher.start();
                questionMatcher.reset();
                questionMatcher.find(currentQuestionEnd);
            }

            // Tìm các đáp án trong khoảng giữa câu hỏi hiện tại và câu hỏi tiếp theo
            answerMatcher.region(currentQuestionEnd, nextQuestionStart);
            while (answerMatcher.find()) {
                String answerKey = answerMatcher.group(1);
                String answerText = answerMatcher.group(2);
                if (StringUtils.isNotBlank(answerKey) && StringUtils.isNotBlank(answerText)) {
                    currentQuestion.getAnswers().add(AnswerModel.builder()
                                    .answer(answerText)
                                    .isCorrect(false)

                            .build());
                }
            }
        }

        if (currentQuestion != null) {
            questions.add(currentQuestion);
        }

        return questions.stream()
                .filter(q -> q.getAnswers().size() >= 2 && q.getAnswers().size() <= 8)
                .collect(Collectors.toList());
    }

}
