package com.davivienda.survey.domain.port;

import com.davivienda.survey.domain.model.Survey;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository {
    Survey save(Survey survey);
    Optional<Survey> findById(String id);
    List<Survey> findAll();
    List<Survey> findByCreatedBy(String userId);
    void deleteById(String id);
}
