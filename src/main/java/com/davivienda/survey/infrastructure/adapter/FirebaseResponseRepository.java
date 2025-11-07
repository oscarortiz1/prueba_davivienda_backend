package com.davivienda.survey.infrastructure.adapter;

import com.davivienda.survey.domain.model.SurveyResponse;
import com.davivienda.survey.domain.port.ResponseRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FirebaseResponseRepository implements ResponseRepository {
    
    private DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
    
    @Override
    public SurveyResponse save(SurveyResponse response) {
        try {
            DatabaseReference ref = getDatabase().child("responses").child(response.getId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", response.getId());
            data.put("surveyId", response.getSurveyId());
            data.put("respondentId", response.getRespondentId());
            data.put("completedAt", response.getCompletedAt().toString());
            
            List<Map<String, Object>> answersData = response.getAnswers().stream()
                .map(answer -> {
                    Map<String, Object> answerMap = new HashMap<>();
                    answerMap.put("questionId", answer.getQuestionId());
                    answerMap.put("value", answer.getValue());
                    return answerMap;
                })
                .collect(Collectors.toList());
            
            data.put("answers", answersData);
            
            ref.setValueAsync(data).get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving response", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<SurveyResponse> findBySurveyId(String surveyId) {
        try {
            DatabaseReference ref = getDatabase().child("responses");
            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
            
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    future.complete(snapshot);
                }
                
                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            
            DataSnapshot snapshot = future.get();
            List<SurveyResponse> responses = new ArrayList<>();
            
            snapshot.getChildren().forEach(child -> {
                Map<String, Object> data = (Map<String, Object>) child.getValue();
                if (data != null && surveyId.equals(data.get("surveyId"))) {
                    SurveyResponse response = mapToResponse(data);
                    responses.add(response);
                }
            });
            
            return responses;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching responses", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private SurveyResponse mapToResponse(Map<String, Object> data) {
        List<SurveyResponse.Answer> answers = new ArrayList<>();
        
        if (data.get("answers") instanceof List) {
            List<Map<String, Object>> answersData = (List<Map<String, Object>>) data.get("answers");
            answers = answersData.stream()
                .map(answerData -> SurveyResponse.Answer.builder()
                    .questionId((String) answerData.get("questionId"))
                    .value((List<String>) answerData.get("value"))
                    .build())
                .collect(Collectors.toList());
        }
        
        return SurveyResponse.builder()
            .id((String) data.get("id"))
            .surveyId((String) data.get("surveyId"))
            .respondentId((String) data.get("respondentId"))
            .completedAt(LocalDateTime.parse((String) data.get("completedAt")))
            .answers(answers)
            .build();
    }
    
    @Override
    public void deleteById(String id) {
        try {
            DatabaseReference ref = getDatabase().child("responses").child(id);
            ref.removeValueAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting response", e);
        }
    }
}
