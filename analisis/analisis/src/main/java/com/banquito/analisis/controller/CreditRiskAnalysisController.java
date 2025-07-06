package com.espe.analisis.crediticio.controller;

import com.espe.analisis.crediticio.controller.dto.EvaluacionCrediticiaDTO;
import com.espe.analisis.crediticio.controller.dto.RevisionAnalistaDTO;
import com.espe.analisis.crediticio.controller.dto.SolicitudAnalisisDTO;
import com.espe.analisis.crediticio.exception.BureauServiceException;
import com.espe.analisis.crediticio.exception.CreditEvaluationException;
import com.espe.analisis.crediticio.exception.NotFoundException;
import com.espe.analisis.crediticio.service.CreditRiskAnalysisService;
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
@RequestMapping("/v1/risk")
@Tag(name = "Análisis Crediticio", description = "APIs para el análisis de riesgo crediticio")
@Slf4j
public class CreditRiskAnalysisController {
    
    private final CreditRiskAnalysisService creditRiskAnalysisService;
    
    public CreditRiskAnalysisController(CreditRiskAnalysisService creditRiskAnalysisService) {
        this.creditRiskAnalysisService = creditRiskAnalysisService;
    }
    
    @PostMapping("/auto-evaluation")
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
            EvaluacionCrediticiaDTO evaluacion = creditRiskAnalysisService.evaluarAutomaticamente(solicitud);
            log.info("Evaluación automática completada para solicitud: {}", solicitud.getIdSolicitud());
            return ResponseEntity.ok(evaluacion);
            
        } catch (BureauServiceException | CreditEvaluationException e) {
            log.error("Error en evaluación automática para solicitud: {}", solicitud.getIdSolicitud(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/analyst-review")
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
            EvaluacionCrediticiaDTO evaluacion = creditRiskAnalysisService.procesarRevisionAnalista(revision);
            log.info("Revisión del analista procesada para evaluación: {}", revision.getIdEvaluacion());
            return ResponseEntity.ok(evaluacion);
            
        } catch (NotFoundException e) {
            log.error("Evaluación no encontrada: {}", revision.getIdEvaluacion(), e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/evaluations/{idSolicitud}")
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
            EvaluacionCrediticiaDTO evaluacion = creditRiskAnalysisService.obtenerEvaluacionPorSolicitud(idSolicitud);
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
    
    @ExceptionHandler({BureauServiceException.class, CreditEvaluationException.class})
    public ResponseEntity<Void> handleServiceException(RuntimeException e) {
        log.error("Error en servicio: {}", e.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}