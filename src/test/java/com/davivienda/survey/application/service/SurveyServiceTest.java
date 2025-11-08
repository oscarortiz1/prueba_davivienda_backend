package com.davivienda.survey.application.service;

import com.davivienda.survey.application.dto.SurveyRequest;
import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.Survey;
import com.davivienda.survey.domain.port.ResponseRepository;
import com.davivienda.survey.domain.port.SurveyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Survey Service Tests")
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private ResponseRepository responseRepository;

    @InjectMocks
    private SurveyService surveyService;

    private Survey testSurvey;
    private SurveyRequest surveyRequest;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "user-123";
        
        testSurvey = Survey.builder()
                .id("survey-123")
                .title("Test Survey")
                .description("Test Description")
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isPublished(false)
                .questions(new ArrayList<>())
                .build();

        surveyRequest = new SurveyRequest();
        surveyRequest.setTitle("New Survey");
        surveyRequest.setDescription("New Description");
    }

    @Test
    @DisplayName("Debería crear una encuesta exitosamente")
    void createSurvey_ShouldCreateSuccessfully() {
        // Arrange
        when(surveyRepository.save(any(Survey.class))).thenReturn(testSurvey);

        // Act
        Survey result = surveyService.createSurvey(surveyRequest, userId);

        // Assert
        assertNotNull(result);
        assertEquals("survey-123", result.getId());
        assertEquals("Test Survey", result.getTitle());
        assertEquals(userId, result.getCreatedBy());
        assertFalse(result.getIsPublished());
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    @DisplayName("Debería obtener una encuesta por ID")
    void getSurvey_ShouldReturnSurvey_WhenExists() {
        // Arrange
        when(surveyRepository.findById(anyString())).thenReturn(Optional.of(testSurvey));

        // Act
        Survey result = surveyService.getSurvey("survey-123");

        // Assert
        assertNotNull(result);
        assertEquals("survey-123", result.getId());
        verify(surveyRepository).findById("survey-123");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la encuesta no existe")
    void getSurvey_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(surveyRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> surveyService.getSurvey("survey-123"));
        verify(surveyRepository).findById("survey-123");
    }

    @Test
    @DisplayName("Debería obtener todas las encuestas del usuario")
    void getUserSurveys_ShouldReturnUserSurveys() {
        // Arrange
        List<Survey> surveys = List.of(testSurvey);
        when(surveyRepository.findByCreatedBy(anyString())).thenReturn(surveys);

        // Act
        List<Survey> result = surveyService.getUserSurveys(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getCreatedBy());
        verify(surveyRepository).findByCreatedBy(userId);
    }

    @Test
    @DisplayName("Debería actualizar una encuesta existente")
    void updateSurvey_ShouldUpdateSuccessfully() {
        // Arrange
        when(surveyRepository.findById(anyString())).thenReturn(Optional.of(testSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(testSurvey);

        surveyRequest.setTitle("Updated Title");
        surveyRequest.setDescription("Updated Description");

        // Act
        Survey result = surveyService.updateSurvey("survey-123", surveyRequest, userId);

        // Assert
        assertNotNull(result);
        verify(surveyRepository).findById("survey-123");
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    @DisplayName("Debería eliminar una encuesta")
    void deleteSurvey_ShouldDeleteSuccessfully() {
        // Arrange
        when(surveyRepository.findById(anyString())).thenReturn(Optional.of(testSurvey));
        doNothing().when(surveyRepository).deleteById(anyString());

        // Act
        surveyService.deleteSurvey("survey-123", userId);

        // Assert
        verify(surveyRepository).findById("survey-123");
        verify(surveyRepository).deleteById("survey-123");
    }

    @Test
    @DisplayName("Debería publicar una encuesta con preguntas")
    void publishSurvey_ShouldPublishSuccessfully_WhenHasQuestions() {
        // Arrange
        Question question = Question.builder()
                .id("q1")
                .title("Test Question")
                .type(com.davivienda.survey.domain.model.QuestionType.TEXT)
                .required(true)
                .build();
        testSurvey.getQuestions().add(question);
        
        when(surveyRepository.findById(anyString())).thenReturn(Optional.of(testSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(testSurvey);

        // Act
        Survey result = surveyService.publishSurvey("survey-123", userId);

        // Assert
        assertNotNull(result);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    @DisplayName("Debería obtener encuestas publicadas")
    void getPublishedSurveys_ShouldReturnOnlyPublished() {
        // Arrange
        testSurvey.setIsPublished(true);
        List<Survey> surveys = List.of(testSurvey);
        when(surveyRepository.findByIsPublished(true)).thenReturn(surveys);

        // Act
        List<Survey> result = surveyService.getPublishedSurveys();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsPublished());
        verify(surveyRepository).findByIsPublished(true);
    }
}
