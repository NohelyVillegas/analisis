package com.espe.analisis.crediticio.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BureauConsultaResponse {
    
    private boolean exitoso;
    private String mensaje;
    private BigDecimal scoreExterno;
    private Integer cuentasActivas;
    private Integer cuentasMorosas;
    private BigDecimal montoTotalAdeudado;
    private Integer numeroDeudasImpagas;
    private BigDecimal diasMoraPromedio;
    private LocalDate fechaPrimeraMora;
    private String jsonCompleto;
}