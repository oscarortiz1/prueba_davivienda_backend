package com.davivienda.survey.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SurveyResponseRequest {
    
    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "Debe ser un correo electrónico válido")
    private String respondentEmail;
    
    @NotEmpty(message = "Debe proporcionar al menos una respuesta")
    private List<AnswerRequest> answers;
    
    @Data
    public static class AnswerRequest {
        @NotBlank(message = "El ID de la pregunta es requerido")
        private String questionId;
        
        @NotEmpty(message = "La respuesta no puede estar vacía")
        private List<String> value;
    }
}
