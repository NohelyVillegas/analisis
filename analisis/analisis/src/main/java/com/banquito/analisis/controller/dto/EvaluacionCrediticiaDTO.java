package com.espe.analisis.crediticio.controller.dto;

import com.espe.analisis.crediticio.model.DecisionFinalAnalista;
import com.espe.analisis.crediticio.model.ResultadoAutomatico;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Respuesta de evaluación crediticia")
public class EvaluacionCrediticiaDTO {
    
    @Schema(description = "ID de la evaluación", example = "123")
    private Long idEvaluacion;
    
    @Schema(description = "ID de la solicitud", example = "456")
    private Long idSolicitud;
    
    @Schema(description = "Resultado automático del motor de reglas")
    private ResultadoAutomatico resultadoAutomatico;
    
    @Schema(description = "Decisión final del analista")
    private DecisionFinalAnalista decisionFinalAnalista;
    
    @Schema(description = "Observaciones del motor de reglas")
    private String observacionesMotorReglas;
    
    @Schema(description = "Justificación del analista")
    private String justificacionAnalista;
    
    @Schema(description = "Fecha de evaluación")
    private LocalDateTime fechaEvaluacion;
}