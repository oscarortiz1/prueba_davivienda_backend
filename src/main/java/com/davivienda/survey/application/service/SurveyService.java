package com.davivienda.survey.application.service;

import com.davivienda.survey.application.constants.AppConstants;
import com.davivienda.survey.application.constants.ErrorMessages;
import com.davivienda.survey.application.dto.SurveyRequest;
import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.Survey;
import com.davivienda.survey.domain.model.SurveyResponse;
import com.davivienda.survey.domain.port.ResponseRepository;
import com.davivienda.survey.domain.port.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {
    
    private final SurveyRepository surveyRepository;
    private final ResponseRepository responseRepository;
    
    public Survey createSurvey(SurveyRequest request, String userId) {
        log.info("Creating survey for user: {}", userId);
        
        LocalDateTime expiresAt = null;
        if (request.getDurationUnit() != null && !AppConstants.DURATION_UNIT_NONE.equals(request.getDurationUnit()) 
            && request.getDurationValue() != null && request.getDurationValue() > 0) {
            LocalDateTime now = LocalDateTime.now();
            switch (request.getDurationUnit()) {
                case AppConstants.DURATION_UNIT_MINUTES:
                    expiresAt = now.plusMinutes(request.getDurationValue());
                    break;
                case AppConstants.DURATION_UNIT_HOURS:
                    expiresAt = now.plusHours(request.getDurationValue());
                    break;
                case AppConstants.DURATION_UNIT_DAYS:
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
                .orElseThrow(() -> new RuntimeException(ErrorMessages.SURVEY_NOT_FOUND));
    }
    
    public Survey getPublicSurvey(String id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorMessages.SURVEY_NOT_FOUND));
        
        if (!Boolean.TRUE.equals(survey.getIsPublished())) {
            throw new RuntimeException(ErrorMessages.SURVEY_NOT_PUBLISHED);
        }
        
        if (survey.getExpiresAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(survey.getExpiresAt())) {
                throw new RuntimeException(ErrorMessages.SURVEY_EXPIRED);
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
            throw new RuntimeException(ErrorMessages.UNAUTHORIZED);
        }
        
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setDurationValue(request.getDurationValue());
        survey.setDurationUnit(request.getDurationUnit());
        
        LocalDateTime expiresAt = null;
        if (request.getDurationUnit() != null && !AppConstants.DURATION_UNIT_NONE.equals(request.getDurationUnit()) 
            && request.getDurationValue() != null && request.getDurationValue() > 0) {
            LocalDateTime now = LocalDateTime.now();
            switch (request.getDurationUnit()) {
                case AppConstants.DURATION_UNIT_MINUTES:
                    expiresAt = now.plusMinutes(request.getDurationValue());
                    break;
                case AppConstants.DURATION_UNIT_HOURS:
                    expiresAt = now.plusHours(request.getDurationValue());
                    break;
                case AppConstants.DURATION_UNIT_DAYS:
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
            throw new RuntimeException(ErrorMessages.UNAUTHORIZED);
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
            throw new RuntimeException(ErrorMessages.UNAUTHORIZED);
        }
        
        if (survey.getQuestions() == null || survey.getQuestions().isEmpty()) {
            throw new RuntimeException(ErrorMessages.SURVEY_NO_QUESTIONS);
        }
        
        survey.setIsPublished(true);
        survey.setUpdatedAt(LocalDateTime.now());
        
        return surveyRepository.save(survey);
    }
    
    public Survey addQuestion(String surveyId, Question question, String userId) {
        Survey survey = getSurvey(surveyId);
        
        if (!survey.getCreatedBy().equals(userId)) {
            throw new RuntimeException(ErrorMessages.UNAUTHORIZED);
        }
        
        // Validar límite de preguntas por encuesta
        if (survey.getQuestions() != null && survey.getQuestions().size() >= AppConstants.MAX_QUESTIONS_PER_SURVEY) {
            throw new RuntimeException(
                String.format(ErrorMessages.SURVEY_MAX_QUESTIONS_EXCEEDED, AppConstants.MAX_QUESTIONS_PER_SURVEY)
            );
        }
        
        // Validar tamaño de imagen en base64 (máximo 2MB)
        if (question.getImageUrl() != null && question.getImageUrl().startsWith(AppConstants.BASE64_IMAGE_PREFIX)) {
            validateBase64ImageSize(question.getImageUrl());
        }
        
        question.setId(UUID.randomUUID().toString());
        question.setSurveyId(surveyId);
        
        if (survey.getQuestions() == null) {
            survey.setQuestions(new ArrayList<>());
        }
        
        log.info("Adding question {} to survey {}", question.getId(), surveyId);
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
            throw new RuntimeException(ErrorMessages.UNAUTHORIZED);
        }
        
        // Validar tamaño de imagen en base64 (máximo 2MB)
        if (updatedQuestion.getImageUrl() != null && updatedQuestion.getImageUrl().startsWith(AppConstants.BASE64_IMAGE_PREFIX)) {
            validateBase64ImageSize(updatedQuestion.getImageUrl());
        }
        
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
            throw new RuntimeException(ErrorMessages.UNAUTHORIZED);
        }
        
        survey.getQuestions().removeIf(q -> q.getId().equals(questionId));
        survey.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(survey.getIsPublished())) {
            survey.setIsPublished(false);
        }
        
        return surveyRepository.save(survey);
    }
    
    /**
     * Valida que el tamaño de la imagen en base64 no exceda los 2MB
     */
    private void validateBase64ImageSize(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return;
        }
        
        // Calcular tamaño aproximado del base64 (cada carácter base64 = 6 bits)
        // Tamaño real = (longitud * 3) / 4
        long estimatedSize = (base64Image.length() * 3L) / 4;
        
        if (estimatedSize > AppConstants.MAX_IMAGE_SIZE_BYTES) {
            log.warn("Image size exceeded: {} bytes", estimatedSize);
            throw new RuntimeException(ErrorMessages.IMAGE_SIZE_EXCEEDED);
        }
    }
}
