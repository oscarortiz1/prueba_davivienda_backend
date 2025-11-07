package com.davivienda.survey.infrastructure.adapter;

import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.QuestionType;
import com.davivienda.survey.domain.model.Survey;
import com.davivienda.survey.domain.port.SurveyRepository;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FirebaseSurveyRepository implements SurveyRepository {
    
    private static final String COLLECTION_NAME = "surveys";
    
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
    
    @Override
    public Survey save(Survey survey) {
        try {
            Map<String, Object> surveyData = surveyToMap(survey);
            
            getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(survey.getId())
                    .set(surveyData)
                    .get();
            
            return survey;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving survey", e);
            throw new RuntimeException("Error saving survey", e);
        }
    }
    
    @Override
    public Optional<Survey> findById(String id) {
        try {
            var document = getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(id)
                    .get()
                    .get();
            
            if (!document.exists()) {
                return Optional.empty();
            }
            
            return Optional.of(documentToSurvey(document.getData()));
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding survey by id", e);
            throw new RuntimeException("Error finding survey", e);
        }
    }
    
    @Override
    public List<Survey> findAll() {
        try {
            var querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .get()
                    .get();
            
            return querySnapshot.getDocuments().stream()
                    .map(doc -> documentToSurvey(doc.getData()))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding all surveys", e);
            throw new RuntimeException("Error finding surveys", e);
        }
    }
    
    @Override
    public List<Survey> findByCreatedBy(String userId) {
        try {
            var querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("createdBy", userId)
                    .get()
                    .get();
            
            return querySnapshot.getDocuments().stream()
                    .map(doc -> documentToSurvey(doc.getData()))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding surveys by user", e);
            throw new RuntimeException("Error finding surveys", e);
        }
    }
    
    @Override
    public void deleteById(String id) {
        try {
            getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(id)
                    .delete()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error deleting survey", e);
            throw new RuntimeException("Error deleting survey", e);
        }
    }
    
    private Map<String, Object> surveyToMap(Survey survey) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", survey.getId());
        data.put("title", survey.getTitle());
        data.put("description", survey.getDescription());
        data.put("createdBy", survey.getCreatedBy());
        data.put("createdAt", survey.getCreatedAt().toString());
        data.put("updatedAt", survey.getUpdatedAt().toString());
        data.put("isPublished", survey.getIsPublished());
        
        if (survey.getQuestions() != null) {
            List<Map<String, Object>> questions = survey.getQuestions().stream()
                    .map(this::questionToMap)
                    .collect(Collectors.toList());
            data.put("questions", questions);
        } else {
            data.put("questions", new ArrayList<>());
        }
        
        return data;
    }
    
    private Map<String, Object> questionToMap(Question question) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", question.getId());
        data.put("surveyId", question.getSurveyId());
        data.put("title", question.getTitle());
        data.put("type", question.getType().name());
        data.put("options", question.getOptions() != null ? question.getOptions() : new ArrayList<>());
        data.put("required", question.getRequired());
        data.put("order", question.getOrder());
        return data;
    }
    
    private Survey documentToSurvey(Map<String, Object> data) {
        List<Question> questions = new ArrayList<>();
        if (data.get("questions") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questionsData = (List<Map<String, Object>>) data.get("questions");
            questions = questionsData.stream()
                    .map(this::mapToQuestion)
                    .collect(Collectors.toList());
        }
        
        return Survey.builder()
                .id((String) data.get("id"))
                .title((String) data.get("title"))
                .description((String) data.get("description"))
                .createdBy((String) data.get("createdBy"))
                .createdAt(LocalDateTime.parse((String) data.get("createdAt")))
                .updatedAt(LocalDateTime.parse((String) data.get("updatedAt")))
                .isPublished((Boolean) data.get("isPublished"))
                .questions(questions)
                .build();
    }
    
    @SuppressWarnings("unchecked")
    private Question mapToQuestion(Map<String, Object> data) {
        return Question.builder()
                .id((String) data.get("id"))
                .surveyId((String) data.get("surveyId"))
                .title((String) data.get("title"))
                .type(QuestionType.valueOf((String) data.get("type")))
                .options((List<String>) data.get("options"))
                .required((Boolean) data.get("required"))
                .order(((Long) data.get("order")).intValue())
                .build();
    }
}
