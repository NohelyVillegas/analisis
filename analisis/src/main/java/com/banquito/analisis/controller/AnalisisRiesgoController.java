package com.banquito.analisis.controller;

import com.banquito.analisis.controller.dto.EvaluacionCrediticiaDTO;
import com.banquito.analisis.controller.dto.RevisionAnalistaDTO;
import com.banquito.analisis.controller.dto.SolicitudAnalisisDTO;
import com.banquito.analisis.exception.ServicioBuroException;
import com.banquito.analisis.exception.EvaluacionCrediticiaExcepcion;
import com.banquito.analisis.exception.NotFoundException;
import com.banquito.analisis.service.AnalisisRiesgoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/riesgo")
@Tag(name = "Análisis Crediticio", description = "APIs para el análisis de riesgo crediticio")
@Slf4j
public class AnalisisRiesgoController {
    
    private final AnalisisRiesgoService analisisRiesgoService;
    
    public AnalisisRiesgoController(AnalisisRiesgoService analisisRiesgoService) {
        this.analisisRiesgoService = analisisRiesgoService;
    }
    
    @PostMapping("/auto-evaluacion")
    @Operation(summary = "Realizar evaluación automática de crédito", 
               description = "Ejecuta el proceso completo de evaluación crediticia incluyendo consulta al buró e informe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evaluación completada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EvaluacionCrediticiaDTO> evaluarAutomaticamente(
            @Valid @RequestBody SolicitudAnalisisDTO solicitud) {
        
        log.info("Recibida solicitud de evaluación automática para: {}", solicitud.getIdSolicitud());
        
        try {
            EvaluacionCrediticiaDTO evaluacion = analisisRiesgoService.evaluarAutomaticamente(solicitud);
            log.info("Evaluación automática completada para solicitud: {}", solicitud.getIdSolicitud());
            return ResponseEntity.ok(evaluacion);
            
        } catch (ServicioBuroException | EvaluacionCrediticiaExcepcion e) {
            log.error("Error en evaluación automática para solicitud: {}", solicitud.getIdSolicitud(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/revision-analista")
    @Operation(summary = "Procesar revisión del analista", 
               description = "Permite al analista revisar y cambiar la decisión automática")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revisión procesada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EvaluacionCrediticiaDTO> procesarRevisionAnalista(
            @Valid @RequestBody RevisionAnalistaDTO revision) {
        
        log.info("Recibida revisión del analista para evaluación: {}", revision.getIdEvaluacion());
        
        try {
            EvaluacionCrediticiaDTO evaluacion = analisisRiesgoService.procesarRevisionAnalista(revision);
            log.info("Revisión del analista procesada para evaluación: {}", revision.getIdEvaluacion());
            return ResponseEntity.ok(evaluacion);
            
        } catch (NotFoundException e) {
            log.error("Evaluación no encontrada: {}", revision.getIdEvaluacion(), e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/evaluaciones/{idSolicitud}")
    @Operation(summary = "Obtener evaluación por solicitud", 
               description = "Recupera la evaluación crediticia de una solicitud específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluacionCrediticiaDTO> obtenerEvaluacionPorSolicitud(
            @Parameter(description = "ID de la solicitud de crédito")
            @PathVariable Long idSolicitud) {
        
        log.info("Consultando evaluación para solicitud: {}", idSolicitud);
        
        try {
            EvaluacionCrediticiaDTO evaluacion = analisisRiesgoService.obtenerEvaluacionPorSolicitud(idSolicitud);
            return ResponseEntity.ok(evaluacion);
            
        } catch (NotFoundException e) {
            log.error("Evaluación no encontrada para solicitud: {}", idSolicitud, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Void> handleNotFoundException(NotFoundException e) {
        log.error("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler({ServicioBuroException.class, EvaluacionCrediticiaExcepcion.class})
    public ResponseEntity<Void> handleServiceException(RuntimeException e) {
        log.error("Error en servicio: {}", e.getMessage());
        return ResponseEntity.internalServerError().build();
    }
} 