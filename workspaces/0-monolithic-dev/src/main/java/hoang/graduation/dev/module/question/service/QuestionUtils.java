package hoang.graduation.dev.module.question.service;

import hoang.graduation.dev.component.CurrentUser;
import hoang.graduation.dev.module.question.entity.QuestionEntity;
import hoang.graduation.dev.module.question.repo.QuestionRepo;
import hoang.graduation.dev.module.user.entity.UserEntity;
import hoang.graduation.dev.share.model.object.QuestionModel;
import hoang.graduation.dev.share.model.request.Question.CreateQuestionRequest;
import hoang.graduation.dev.share.model.request.Question.UpdateQuestionRequest;
import hoang.graduation.dev.share.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionUtils {

    private static final Logger logger = LoggerFactory.getLogger(QuestionUtils.class);
    private final QuestionRepo questionRepo;

    public QuestionEntity create(CreateQuestionRequest request, String username) {
        QuestionEntity newRecord = MappingUtils.mapObject(request, QuestionEntity.class);
        newRecord.setId(UUID.randomUUID().toString());
        String questionCode = generateQuestionCode();
        newRecord.setCode(questionCode);
        newRecord.setCreatedAt(new Date());
        newRecord.setUpdatedAt(new Date());
        newRecord.setCreatedBy(username);
        newRecord.setUpdatedBy(username);
        newRecord.getAnswers().forEach(q -> {
            q.setQuestionCode(questionCode);
            q.setId(UUID.randomUUID().toString());
        });
        return questionRepo.save(newRecord);
    }

    public QuestionEntity update(UpdateQuestionRequest request, String username) {
        QuestionEntity newRecord = questionRepo.findById(request.getId()).orElse(null);
        if (newRecord == null) {
            return null;
        }
        newRecord.setAttachmentPath(request.getAttachmentPath());
        newRecord.setQuestion(request.getQuestion());
        newRecord.setAnswers(request.getAnswers());
        newRecord.setType(request.getType());
        newRecord.setUpdatedAt(new Date());
        newRecord.setUpdatedBy(request.getUpdatedBy());
        newRecord.setCreatedBy(username);
        newRecord.setUpdatedBy(username);
        return questionRepo.save(newRecord);
    }

    public List<QuestionEntity> update(List<QuestionModel> questions) {
        List<QuestionEntity> response = new ArrayList<>();
        UserEntity crnt = CurrentUser.get();
        if (crnt == null) {
            return null;
        }
        for (QuestionModel q : questions) {
            if (StringUtils.isNotBlank(q.getId())) {
                response.add(update(MappingUtils.mapObject(q, UpdateQuestionRequest.class), crnt.getEmail()));
                continue;
            }
            response.add(create(MappingUtils.mapObject(q, CreateQuestionRequest.class), crnt.getEmail()));
        }
        return response;
    }


    public void delete(String id) {
        questionRepo.deleteById(id);
    }

    public void deleteAll(List<String> id) {
        questionRepo.deleteAllById(id);
    }

    public void deleteAllByIds(List<String> ids) {
        List<QuestionEntity> Questions = questionRepo.findAllById(ids);
        questionRepo.deleteAll(Questions);
    }

    public String generateQuestionCode() {
        return "Q" + String.valueOf(Year.now().getValue()).substring(2) + "0" + questionRepo.count() + 1;
    }
}
