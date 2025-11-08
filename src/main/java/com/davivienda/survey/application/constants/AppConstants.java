package com.davivienda.survey.application.constants;

/**
 * Constantes de configuración de la aplicación
 */
public final class AppConstants {
    
    // Image configuration
    public static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024; // 2MB
    public static final String BASE64_IMAGE_PREFIX = "data:image";
    
    // Survey configuration
    public static final int MAX_QUESTIONS_PER_SURVEY = 100;
    public static final int MAX_RESPONSES_PAGE_SIZE = 50;
    
    // Duration units
    public static final String DURATION_UNIT_NONE = "none";
    public static final String DURATION_UNIT_MINUTES = "minutes";
    public static final String DURATION_UNIT_HOURS = "hours";
    public static final String DURATION_UNIT_DAYS = "days";
    
    private AppConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}
