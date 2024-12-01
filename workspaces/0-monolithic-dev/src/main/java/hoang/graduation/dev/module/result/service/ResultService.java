package hoang.graduation.dev.module.result.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.exam.entity.ExamEntity;
import hoang.graduation.dev.module.exam.entity.ExamSessionEntity;
import hoang.graduation.dev.module.exam.repo.ExamRepo;
import hoang.graduation.dev.module.exam.repo.ExamSessionRepo;
import hoang.graduation.dev.module.result.entity.ResultDetailEntity;
import hoang.graduation.dev.module.result.repo.ResultDetailRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.share.constant.PointType;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.model.object.AnswerResultModel;
import hoang.graduation.share.model.object.QuestionResultModel;
import hoang.graduation.share.model.request.result.SubmitResultRequest;
import hoang.graduation.share.model.response.WrapResponse;
import hoang.graduation.share.utils.DateTimesUtils;
import hoang.graduation.share.utils.MappingUtils;
import hoang.graduation.share.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class ResultService {
    private final ResultDetailRepo resultDetailRepo;
    private final LocalizationUtils localizationUtils;
    private final ExamSessionRepo examSessionRepo;
    private final RabbitTemplate rabbitTemplate;
    private final ExamRepo examRepo;

    public WrapResponse<?> startExam(String examSessionId) {
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        ExamSessionEntity examSession = examSessionRepo.findById(examSessionId).orElse(null);

        if (examSession == null) {
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_SESSION_NOT_FOUND)).build();
        }
        ExamEntity exam = examRepo.findByCode(examSession.getExamCode());
        if (exam == null) {
            return WrapResponse.builder().isSuccess(false).status(HttpStatus.BAD_REQUEST).message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_NOT_FOUND)).build();
        }
        List<QuestionResultModel> questionResults = MappingUtils.mapList(exam.getQuestions(), QuestionResultModel.class);
        int randomAmount = examSession.getRandomAmount();
        int sequenceNumber = examSession.getStartSequenceNumber() + 1;
        String examRandomCode = exam.getCode() + "0" + sequenceNumber % randomAmount;

        questionResults.forEach(q -> {
            q.getAnswers().forEach(a -> {
                a.setChosen(false);
            });
        });
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(resultDetailRepo.save(ResultDetailEntity.builder()
                        .id(examSessionId + "&" + crnt.getId())
                        .examRandomCode(examRandomCode)
                        .studentId(crnt.getId())
                        .studentCode(crnt.getCode())
                        .studentName(crnt.getName())
                        .studentEmail(crnt.getEmail())
                        .examSessionId(examSessionId)
                        .examSessionAuthor(examSession.getCreatedBy())
                        .examCode(exam.getCode())
                        .questionResults(random(sequenceNumber % randomAmount, questionResults))
                        .startAt(new Date())
                        .submitAt(new Date())
                        .correctAnswer(0)
                        .score(0)
                        .wrongAnswer(0)
                        .notAnswer(0)
                        .timeTracking(0)
                        .build()))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_STARTED))
                .build();
    }

    public WrapResponse<?> submitExam(String resultId, SubmitResultRequest request) {
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        ResultDetailEntity newResult = resultDetailRepo.findById(resultId).orElse(null);
        assert newResult != null;
        newResult.setSubmitAt(new Date());
        newResult.setTimeTracking(DateTimesUtils.calculateMinutesDifference(new Date(), newResult.getStartAt()));
        newResult.setQuestionResults(request.getQuestionResults());

        if (CollectionUtils.isEmpty(request.getQuestionResults())) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EXAM_RESULT_NOT_EXISTS))
                    .build();
        }

        int totalQuestion = request.getQuestionResults().size();
        int totalAnswer = request.getQuestionResults().stream().mapToInt(x -> x.getAnswers().size()).sum();

        int totalCorrectAnswer = 0;
        int totalCorrectQuestion = 0;

        int totalIncorrectQuestion = 0;
        int totalIncorrectAnswer = 0;

        for (QuestionResultModel r : request.getQuestionResults()) {
            int totalCorrect = r.getAnswers().stream().filter(AnswerResultModel::isCorrect).toList().size();
            int totalSubmitCorrect = 0;
            for (AnswerResultModel a : r.getAnswers()) {
                if (!a.isChosen()) continue;
                if (a.isCorrect()) {
                    totalCorrectAnswer++;
                    totalSubmitCorrect++;
                } else totalIncorrectAnswer++;
            }
            if (totalCorrect != 0 && totalCorrect == totalSubmitCorrect) {
                totalCorrectQuestion++;
                continue;
            }
            totalIncorrectQuestion++;
        }
        if (examSessionRepo.getPointTypeById(newResult.getExamSessionId()) == PointType.FOLLOW_ANSWER) {
            newResult.setScore(MathUtils.calculateScore(totalCorrectAnswer, totalAnswer));
        } else newResult.setScore(MathUtils.calculateScore(totalCorrectQuestion, totalQuestion));

        newResult.setCorrectAnswer(totalCorrectAnswer);
        newResult.setWrongAnswer(totalIncorrectAnswer);
        newResult.setCorrectQuestion(totalCorrectQuestion);
        newResult.setWrongQuestion(totalIncorrectQuestion);

        resultDetailRepo.save(newResult);
        rabbitTemplate.convertAndSend(RabbitQueueMessage.QUEUE_SEND_SUBMIT_EXAM_SESSION, newResult.getId());
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(newResult)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SUBMIT_EXAM_SUCCESSFULLY))
                .build();
    }

    public List<QuestionResultModel> random(int seed, List<QuestionResultModel> questionResults) {
        Random random = new Random(seed);
        Collections.shuffle(questionResults, random);

        for (QuestionResultModel question : questionResults) {
            Collections.shuffle(question.getAnswers(), random);
        }

        return questionResults;
    }
}
