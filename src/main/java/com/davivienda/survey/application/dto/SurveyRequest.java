package com.davivienda.survey.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SurveyRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
}
