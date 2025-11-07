package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.dto.SurveyRequest;
import com.davivienda.survey.application.service.AuthService;
import com.davivienda.survey.application.service.SurveyService;
import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.Survey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyController {
    
    private final SurveyService surveyService;
    private final AuthService authService;
    
    @PostMapping
    public ResponseEntity<Survey> createSurvey(
            @Valid @RequestBody SurveyRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.createSurvey(request, userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurvey(@PathVariable String id) {
        return ResponseEntity.ok(surveyService.getSurvey(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }
    
    @GetMapping("/my-surveys")
    public ResponseEntity<List<Survey>> getUserSurveys(Authentication authentication) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.getUserSurveys(userId));
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<Survey>> getPublishedSurveys() {
        return ResponseEntity.ok(surveyService.getPublishedSurveys());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Survey> updateSurvey(
            @PathVariable String id,
            @Valid @RequestBody SurveyRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.updateSurvey(id, request, userId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(
            @PathVariable String id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        surveyService.deleteSurvey(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/publish")
    public ResponseEntity<Survey> publishSurvey(
            @PathVariable String id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.publishSurvey(id, userId));
    }
    
    @PostMapping("/{surveyId}/questions")
    public ResponseEntity<Survey> addQuestion(
            @PathVariable String surveyId,
            @RequestBody Question question,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.addQuestion(surveyId, question, userId));
    }
    
    @PutMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Survey> updateQuestion(
            @PathVariable String surveyId,
            @PathVariable String questionId,
            @RequestBody Question question,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.updateQuestion(surveyId, questionId, question, userId));
    }
    
    @DeleteMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Survey> deleteQuestion(
            @PathVariable String surveyId,
            @PathVariable String questionId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.deleteQuestion(surveyId, questionId, userId));
    }
}
