package com.davivienda.survey.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private String id;
    private String surveyId;
    private String title;
    private QuestionType type;
    private List<String> options;
    private Boolean required;
    private Integer order;
    private String imageUrl;
}
