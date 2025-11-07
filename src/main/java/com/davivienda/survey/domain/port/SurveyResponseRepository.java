package com.davivienda.survey.domain.port;

import com.davivienda.survey.domain.model.SurveyResponse;

import java.util.List;
import java.util.Optional;

public interface SurveyResponseRepository {
    SurveyResponse save(SurveyResponse response);
    Optional<SurveyResponse> findById(String id);
    List<SurveyResponse> findBySurveyId(String surveyId);
    void deleteById(String id);
}
