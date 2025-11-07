package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.dto.SurveyResponseRequest;
import com.davivienda.survey.application.service.ResponseService;
import com.davivienda.survey.domain.model.SurveyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class ResponseController {
    
    private final ResponseService responseService;
    
    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<SurveyResponse> submitResponse(
            @PathVariable String surveyId,
            @Valid @RequestBody SurveyResponseRequest request
    ) {
        return ResponseEntity.ok(responseService.submitResponse(surveyId, request));
    }
    
    @GetMapping("/{surveyId}/responses")
    public ResponseEntity<List<SurveyResponse>> getSurveyResponses(@PathVariable String surveyId) {
        return ResponseEntity.ok(responseService.getSurveyResponses(surveyId));
    }
}
