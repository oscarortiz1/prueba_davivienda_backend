package com.davivienda.survey.domain.port;

import com.davivienda.survey.domain.model.SurveyResponse;

import java.util.List;

public interface ResponseRepository {
    SurveyResponse save(SurveyResponse response);
    List<SurveyResponse> findBySurveyId(String surveyId);
    void deleteById(String id);
}
