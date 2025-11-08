package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.dto.SurveyResponseRequest;
import com.davivienda.survey.application.service.ResponseService;
import com.davivienda.survey.domain.model.SurveyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
@Tag(name = "Respuestas", description = "Endpoints para enviar y consultar respuestas a encuestas")
public class ResponseController {
    
    private final ResponseService responseService;
    
    @Operation(
        summary = "Enviar respuesta a una encuesta",
        description = "Permite a cualquier usuario (autenticado o anónimo) enviar una respuesta a una encuesta publicada. " +
                     "Se debe proporcionar el email del participante y las respuestas a todas las preguntas obligatorias."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Respuesta enviada exitosamente",
            content = @Content(schema = @Schema(implementation = SurveyResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Verifique que todas las preguntas obligatorias estén respondidas"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Encuesta no encontrada"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "La encuesta no está publicada o ha expirado"
        )
    })
    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<SurveyResponse> submitResponse(
            @Parameter(description = "ID de la encuesta", required = true)
            @PathVariable String surveyId,
            @Valid @RequestBody SurveyResponseRequest request
    ) {
        return ResponseEntity.ok(responseService.submitResponse(surveyId, request));
    }
    
    @Operation(
        summary = "Obtener respuestas de una encuesta",
        description = "Devuelve todas las respuestas enviadas a una encuesta específica. " +
                     "Este endpoint es público para permitir verificar si un usuario ya respondió."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Respuestas obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Encuesta no encontrada"
        )
    })
    @GetMapping("/{surveyId}/responses")
    public ResponseEntity<List<SurveyResponse>> getSurveyResponses(
            @Parameter(description = "ID de la encuesta", required = true)
            @PathVariable String surveyId
    ) {
        return ResponseEntity.ok(responseService.getSurveyResponses(surveyId));
    }
}
