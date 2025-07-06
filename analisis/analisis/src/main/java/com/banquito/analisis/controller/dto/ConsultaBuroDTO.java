package com.espe.analisis.crediticio.controller.dto;

import com.espe.analisis.crediticio.model.EstadoConsulta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Respuesta de consulta al buró")
public class ConsultaBuroDTO {
    
    @Schema(description = "ID de la consulta", example = "789")
    private Long idConsulta;
    
    @Schema(description = "ID de la solicitud", example = "456")
    private Long idSolicitud;
    
    @Schema(description = "Estado de la consulta")
    private EstadoConsulta estadoConsulta;
    
    @Schema(description = "Score externo del buró", example = "720")
    private BigDecimal scoreExterno;
    
    @Schema(description = "Número de cuentas activas", example = "5")
    private Integer cuentasActivas;
    
    @Schema(description = "Número de cuentas morosas", example = "1")
    private Integer cuentasMorosas;
    
    @Schema(description = "Monto total moroso", example = "1500.00")
    private BigDecimal montoMorosoTotal;
    
    @Schema(description = "Fecha de la consulta")
    private LocalDateTime fechaConsulta;
}