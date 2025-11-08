package com.davivienda.survey.application.constants;

/**
 * Constantes para mensajes de error del sistema
 */
public final class ErrorMessages {
    
    // Survey errors
    public static final String SURVEY_NOT_FOUND = "Survey not found";
    public static final String SURVEY_NOT_PUBLISHED = "Survey is not published";
    public static final String SURVEY_EXPIRED = "Esta encuesta ha expirado y ya no acepta respuestas";
    public static final String SURVEY_NO_QUESTIONS = "Cannot publish survey without questions";
    public static final String SURVEY_MAX_QUESTIONS_EXCEEDED = "No se pueden agregar más de %d preguntas a una encuesta";
    
    // Authorization errors
    public static final String UNAUTHORIZED = "Unauthorized";
    
    // Image errors
    public static final String IMAGE_SIZE_EXCEEDED = "La imagen excede el tamaño máximo de 2MB";
    
    // Response errors
    public static final String RESPONSE_NOT_FOUND = "Response not found";
    
    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}
