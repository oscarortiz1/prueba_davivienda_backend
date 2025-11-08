package com.davivienda.survey.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveyRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private Integer durationValue;
    
    private String durationUnit;
    private LocalDateTime expiresAt;
}
