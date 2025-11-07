package com.davivienda.survey.infrastructure.adapter;

import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.QuestionType;
import com.davivienda.survey.domain.model.Survey;
import com.davivienda.survey.domain.port.SurveyRepository;
import com.google.firebase.database.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FirebaseSurveyRepository implements SurveyRepository {
    
    private static final String COLLECTION_NAME = "surveys";
    
    private DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
    
    @Override
    public Survey save(Survey survey) {
        try {
            Map<String, Object> surveyData = surveyToMap(survey);
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .child(survey.getId())
                    .setValue(surveyData, (error, ref) -> {
                        if (error != null) {
                            future.completeExceptionally(error.toException());
                        } else {
                            future.complete(null);
                        }
                    });
            
            future.get();
            return survey;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving survey", e);
            throw new RuntimeException("Error saving survey", e);
        }
    }
    
    @Override
    public Optional<Survey> findById(String id) {
        try {
            CompletableFuture<Optional<Survey>> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                future.complete(Optional.empty());
                                return;
                            }
                            
                            @SuppressWarnings("unchecked")
                            Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                            future.complete(Optional.of(mapToSurvey(data)));
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
            
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding survey by id", e);
            throw new RuntimeException("Error finding survey", e);
        }
    }
    
    @Override
    public List<Survey> findAll() {
        try {
            CompletableFuture<List<Survey>> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            List<Survey> surveys = new ArrayList<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> data = (Map<String, Object>) child.getValue();
                                surveys.add(mapToSurvey(data));
                            }
                            future.complete(surveys);
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
            
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding all surveys", e);
            throw new RuntimeException("Error finding surveys", e);
        }
    }
    
    @Override
    public List<Survey> findByCreatedBy(String userId) {
        try {
            CompletableFuture<List<Survey>> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .orderByChild("createdBy")
                    .equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            List<Survey> surveys = new ArrayList<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> data = (Map<String, Object>) child.getValue();
                                surveys.add(mapToSurvey(data));
                            }
                            future.complete(surveys);
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
            
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding surveys by user", e);
            throw new RuntimeException("Error finding surveys", e);
        }
    }
    
    @Override
    public List<Survey> findByIsPublished(boolean isPublished) {
        try {
            CompletableFuture<List<Survey>> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .orderByChild("isPublished")
                    .equalTo(isPublished)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            List<Survey> surveys = new ArrayList<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> data = (Map<String, Object>) child.getValue();
                                surveys.add(mapToSurvey(data));
                            }
                            future.complete(surveys);
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
            
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error finding surveys by published status", e);
            throw new RuntimeException("Error finding published surveys", e);
        }
    }
    
    @Override
    public void deleteById(String id) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            
            getDatabase()
                    .child(COLLECTION_NAME)
                    .child(id)
                    .removeValue((error, ref) -> {
                        if (error != null) {
                            future.completeExceptionally(error.toException());
                        } else {
                            future.complete(null);
                        }
                    });
            
            future.get();
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
    
    private Survey mapToSurvey(Map<String, Object> data) {
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
        Object orderValue = data.get("order");
        int order = 0;
        
        if (orderValue instanceof Long) {
            order = ((Long) orderValue).intValue();
        } else if (orderValue instanceof Integer) {
            order = (Integer) orderValue;
        }
        
        return Question.builder()
                .id((String) data.get("id"))
                .surveyId((String) data.get("surveyId"))
                .title((String) data.get("title"))
                .type(QuestionType.valueOf((String) data.get("type")))
                .options((List<String>) data.get("options"))
                .required((Boolean) data.get("required"))
                .order(order)
                .build();
    }
}
