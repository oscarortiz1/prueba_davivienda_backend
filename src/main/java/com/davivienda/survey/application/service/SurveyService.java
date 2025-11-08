package com.davivienda.survey.application.service;

import com.davivienda.survey.application.dto.SurveyRequest;
import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.Survey;
import com.davivienda.survey.domain.model.SurveyResponse;
import com.davivienda.survey.domain.port.ResponseRepository;
import com.davivienda.survey.domain.port.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {
    
    private final SurveyRepository surveyRepository;
    private final ResponseRepository responseRepository;
    
    public Survey createSurvey(SurveyRequest request, String userId) {
        LocalDateTime expiresAt = null;
        if (request.getDurationUnit() != null && !"none".equals(request.getDurationUnit()) 
            && request.getDurationValue() != null && request.getDurationValue() > 0) {
            LocalDateTime now = LocalDateTime.now();
            switch (request.getDurationUnit()) {
                case "minutes":
                    expiresAt = now.plusMinutes(request.getDurationValue());
                    break;
                case "hours":
                    expiresAt = now.plusHours(request.getDurationValue());
                    break;
                case "days":
                    expiresAt = now.plusDays(request.getDurationValue());
                    break;
            }
        }
        
        Survey survey = Survey.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isPublished(false)
                .durationValue(request.getDurationValue())
                .durationUnit(request.getDurationUnit())
                .expiresAt(expiresAt)
                .questions(new ArrayList<>())
                .build();
        
        return surveyRepository.save(survey);
    }
    
    public Survey getSurvey(String id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
    }
    
    public Survey getPublicSurvey(String id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        if (!Boolean.TRUE.equals(survey.getIsPublished())) {
            throw new RuntimeException("Survey is not published");
        }
        
        if (survey.getExpiresAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(survey.getExpiresAt())) {
                throw new RuntimeException("Esta encuesta ha expirado y ya no acepta respuestas");
            }
        }
        
        return survey;
    }
    
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }
    
    public List<Survey> getUserSurveys(String userId) {
        return surveyRepository.findByCreatedBy(userId);
    }
    
    public List<Survey> getPublishedSurveys() {
        return surveyRepository.findByIsPublished(true);
    }
    
    public Survey updateSurvey(String id, SurveyRequest request, String userId) {
        Survey survey = getSurvey(id);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setDurationValue(request.getDurationValue());
        survey.setDurationUnit(request.getDurationUnit());
        
        LocalDateTime expiresAt = null;
        if (request.getDurationUnit() != null && !"none".equals(request.getDurationUnit()) 
            && request.getDurationValue() != null && request.getDurationValue() > 0) {
            LocalDateTime now = LocalDateTime.now();
            switch (request.getDurationUnit()) {
                case "minutes":
                    expiresAt = now.plusMinutes(request.getDurationValue());
                    break;
                case "hours":
                    expiresAt = now.plusHours(request.getDurationValue());
                    break;
                case "days":
                    expiresAt = now.plusDays(request.getDurationValue());
                    break;
            }
        }
        survey.setExpiresAt(expiresAt);
        
        survey.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(survey.getIsPublished())) {
            survey.setIsPublished(false);
        }
        
        return surveyRepository.save(survey);
    }
    
    public void deleteSurvey(String id, String userId) {
        Survey survey = getSurvey(id);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        List<SurveyResponse> responses = responseRepository.findBySurveyId(id);
        for (SurveyResponse response : responses) {
            responseRepository.deleteById(response.getId());
        }
        
        surveyRepository.deleteById(id);
    }
    
    public Survey publishSurvey(String id, String userId) {
        Survey survey = getSurvey(id);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (survey.getQuestions() == null || survey.getQuestions().isEmpty()) {
            throw new RuntimeException("Cannot publish survey without questions");
        }
        
        survey.setIsPublished(true);
        survey.setUpdatedAt(LocalDateTime.now());
        
        return surveyRepository.save(survey);
    }
    
    public Survey addQuestion(String surveyId, Question question, String userId) {
        Survey survey = getSurvey(surveyId);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        System.out.println("🔍 DEBUG - Pregunta recibida: " + question.getTitle());
        System.out.println("🔍 DEBUG - ImageUrl recibido: " + (question.getImageUrl() != null ? "Imagen presente (base64)" : "Sin imagen"));
        
        question.setId(UUID.randomUUID().toString());
        question.setSurveyId(surveyId);
        
        if (survey.getQuestions() == null) {
            survey.setQuestions(new ArrayList<>());
        }
        
        survey.getQuestions().add(question);
        survey.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(survey.getIsPublished())) {
            survey.setIsPublished(false);
        }
        
        return surveyRepository.save(survey);
    }
    
    public Survey updateQuestion(String surveyId, String questionId, Question updatedQuestion, String userId) {
        Survey survey = getSurvey(surveyId);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        System.out.println("🔍 DEBUG - Actualizar pregunta: " + updatedQuestion.getTitle());
        System.out.println("🔍 DEBUG - ImageUrl recibido: " + (updatedQuestion.getImageUrl() != null ? "Imagen presente (base64)" : "Sin imagen"));
        
        List<Question> questions = survey.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(questionId)) {
                updatedQuestion.setId(questionId);
                updatedQuestion.setSurveyId(surveyId);
                questions.set(i, updatedQuestion);
                break;
            }
        }
        
        survey.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(survey.getIsPublished())) {
            survey.setIsPublished(false);
        }
        
        return surveyRepository.save(survey);
    }
    
    public Survey deleteQuestion(String surveyId, String questionId, String userId) {
        Survey survey = getSurvey(surveyId);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        survey.getQuestions().removeIf(q -> q.getId().equals(questionId));
        survey.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(survey.getIsPublished())) {
            survey.setIsPublished(false);
        }
        
        return surveyRepository.save(survey);
    }
}
