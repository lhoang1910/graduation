package hoang.graduation.dev.module.result.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.config.LocalizationUtils;
import hoang.graduation.dev.messages.MessageKeys;
import hoang.graduation.dev.module.exam.repo.ExamSessionRepo;
import hoang.graduation.dev.module.result.entity.ResultDetailEntity;
import hoang.graduation.dev.module.result.repo.ResultDetailRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.share.constant.PointType;
import hoang.graduation.share.constant.rm.RabbitQueueMessage;
import hoang.graduation.share.model.object.AnswerResultModel;
import hoang.graduation.share.model.object.QuestionResultModel;
import hoang.graduation.share.model.request.result.StartResultRequest;
import hoang.graduation.share.model.request.result.SubmitResultRequest;
import hoang.graduation.share.model.response.WrapResponse;
import hoang.graduation.share.utils.DateTimeUtils;
import hoang.graduation.share.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class ResultService {
    private final ResultDetailRepo resultDetailRepo;
    private final LocalizationUtils localizationUtils;
    private final ExamSessionRepo examSessionRepo;
    private final RabbitTemplate rabbitTemplate;

    public WrapResponse<?> startExam(String examSessionId) {
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return WrapResponse.builder()
                    .isSuccess(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                    .build();
        }
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(resultDetailRepo.save(ResultDetailEntity.builder()
                        .id(examSessionId + "&" + crnt.getId())
                        .studentId(crnt.getId())
                        .studentCode(crnt.getCode())
                        .studentName(crnt.getName())
                        .studentEmail(crnt.getEmail())
                        .examSessionId(examSessionId)
                        .examSessionAuthor(examSessionRepo.findCreatedByById(examSessionId))
                        .examCode(examSessionRepo.findExamCodeById(examSessionId))
                        .startAt(new Date())
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
        newResult.setTimeTracking(DateTimeUtils.calculateMinutesDifference(new Date(), newResult.getStartAt()));
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
}
