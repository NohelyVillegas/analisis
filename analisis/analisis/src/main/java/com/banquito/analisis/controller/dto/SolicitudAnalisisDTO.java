package com.espe.analisis.crediticio.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Solicitud de análisis crediticio")
public class SolicitudAnalisisDTO {
    
    @NotNull(message = "El ID de solicitud es obligatorio")
    @Schema(description = "ID de la solicitud de crédito", example = "12345")
    private Long idSolicitud;
    
    @NotNull(message = "Los ingresos son obligatorios")
    @Positive(message = "Los ingresos deben ser positivos")
    @Schema(description = "Ingresos mensuales del solicitante", example = "2500.00")
    private BigDecimal ingresos;
    
    @NotNull(message = "Los egresos son obligatorios")
    @Positive(message = "Los egresos deben ser positivos")
    @Schema(description = "Egresos mensuales del solicitante", example = "1500.00")
    private BigDecimal egresos;
    
    @NotNull(message = "La cuota mensual es obligatoria")
    @Positive(message = "La cuota mensual debe ser positiva")
    @Schema(description = "Cuota mensual del préstamo solicitado", example = "300.00")
    private BigDecimal cuotaMensual;
}