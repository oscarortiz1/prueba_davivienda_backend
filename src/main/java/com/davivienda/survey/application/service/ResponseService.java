package com.davivienda.survey.application.service;

import com.davivienda.survey.application.dto.SurveyResponseRequest;
import com.davivienda.survey.domain.model.Survey;
import com.davivienda.survey.domain.model.SurveyResponse;
import com.davivienda.survey.domain.port.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseService {
    
    private final ResponseRepository responseRepository;
    private final SurveyService surveyService;
    
    public SurveyResponse submitResponse(String surveyId, SurveyResponseRequest request) {
        Survey survey = surveyService.getSurvey(surveyId);
        
        if (survey.getExpiresAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(survey.getExpiresAt())) {
                throw new RuntimeException("Esta encuesta ha expirado y ya no acepta respuestas");
            }
        }
        
        List<SurveyResponse.Answer> answers = request.getAnswers().stream()
                .map(a -> SurveyResponse.Answer.builder()
                        .questionId(a.getQuestionId())
                        .value(a.getValue())
                        .build())
                .collect(Collectors.toList());
        
        SurveyResponse response = SurveyResponse.builder()
                .id(UUID.randomUUID().toString())
                .surveyId(surveyId)
                .respondentId(request.getRespondentEmail())
                .answers(answers)
                .completedAt(LocalDateTime.now())
                .build();
        
        return responseRepository.save(response);
    }
    
    public List<SurveyResponse> getSurveyResponses(String surveyId) {
        return responseRepository.findBySurveyId(surveyId);
    }
}
