package com.davivienda.survey.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    private String id;
    private String questionId;
    private String surveyId;
    private Object value; 
    private String respondentId;
    private LocalDateTime createdAt;
}
