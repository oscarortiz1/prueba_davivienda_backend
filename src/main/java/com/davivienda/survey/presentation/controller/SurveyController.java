package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.dto.SurveyRequest;
import com.davivienda.survey.application.service.AuthService;
import com.davivienda.survey.application.service.SurveyService;
import com.davivienda.survey.domain.model.Question;
import com.davivienda.survey.domain.model.Survey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
@Tag(name = "Encuestas", description = "Endpoints para gestionar encuestas (crear, editar, publicar, eliminar)")
public class SurveyController {
    
    private final SurveyService surveyService;
    private final AuthService authService;
    
    @Operation(
        summary = "Crear nueva encuesta",
        description = "Crea una nueva encuesta en estado borrador. Requiere autenticación.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Encuesta creada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Survey> createSurvey(
            @Valid @RequestBody SurveyRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.createSurvey(request, userId));
    }
    
    @Operation(
        summary = "Obtener encuesta por ID",
        description = "Obtiene los detalles completos de una encuesta. Requiere autenticación.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurvey(
            @Parameter(description = "ID de la encuesta") @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(surveyService.getSurvey(id));
    }
    
    @Operation(
        summary = "Obtener encuesta pública",
        description = "Obtiene una encuesta publicada sin requerir autenticación. Solo funciona para encuestas publicadas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Encuesta obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "La encuesta no está publicada"),
        @ApiResponse(responseCode = "404", description = "Encuesta no encontrada")
    })
    @GetMapping("/public/{id}")
    public ResponseEntity<Survey> getPublicSurvey(
            @Parameter(description = "ID de la encuesta") @PathVariable String id
    ) {
        return ResponseEntity.ok(surveyService.getPublicSurvey(id));
    }
    
    @Operation(
        summary = "Listar todas las encuestas",
        description = "Obtiene la lista completa de encuestas del sistema. Requiere autenticación.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }
    
    @Operation(
        summary = "Obtener mis encuestas",
        description = "Obtiene las encuestas creadas por el usuario autenticado.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/my-surveys")
    public ResponseEntity<List<Survey>> getUserSurveys(Authentication authentication) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.getUserSurveys(userId));
    }
    
    @Operation(
        summary = "Listar encuestas publicadas",
        description = "Obtiene todas las encuestas publicadas disponibles para responder. No requiere autenticación."
    )
    @GetMapping("/published")
    public ResponseEntity<List<Survey>> getPublishedSurveys() {
        return ResponseEntity.ok(surveyService.getPublishedSurveys());
    }
    
    @Operation(
        summary = "Actualizar encuesta",
        description = "Actualiza los datos de una encuesta existente. Solo el creador puede editarla.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    public ResponseEntity<Survey> updateSurvey(
            @Parameter(description = "ID de la encuesta") @PathVariable String id,
            @Valid @RequestBody SurveyRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.updateSurvey(id, request, userId));
    }
    
    @Operation(
        summary = "Eliminar encuesta",
        description = "Elimina una encuesta y todas sus respuestas. Solo el creador puede eliminarla.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(
            @Parameter(description = "ID de la encuesta") @PathVariable String id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        surveyService.deleteSurvey(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Publicar encuesta",
        description = "Publica una encuesta para que esté disponible públicamente. Debe tener al menos una pregunta.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}/publish")
    public ResponseEntity<Survey> publishSurvey(
            @Parameter(description = "ID de la encuesta") @PathVariable String id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.publishSurvey(id, userId));
    }
    
    @Operation(
        summary = "Agregar pregunta a encuesta",
        description = "Agrega una nueva pregunta a la encuesta. Máximo 100 preguntas por encuesta.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{surveyId}/questions")
    public ResponseEntity<Survey> addQuestion(
            @Parameter(description = "ID de la encuesta") @PathVariable String surveyId,
            @RequestBody Question question,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.addQuestion(surveyId, question, userId));
    }
    
    @Operation(
        summary = "Actualizar pregunta",
        description = "Actualiza una pregunta existente de la encuesta.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Survey> updateQuestion(
            @Parameter(description = "ID de la encuesta") @PathVariable String surveyId,
            @Parameter(description = "ID de la pregunta") @PathVariable String questionId,
            @RequestBody Question question,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.updateQuestion(surveyId, questionId, question, userId));
    }
    
    @Operation(
        summary = "Eliminar pregunta",
        description = "Elimina una pregunta de la encuesta.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Survey> deleteQuestion(
            @Parameter(description = "ID de la encuesta") @PathVariable String surveyId,
            @Parameter(description = "ID de la pregunta") @PathVariable String questionId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(surveyService.deleteQuestion(surveyId, questionId, userId));
    }
}
