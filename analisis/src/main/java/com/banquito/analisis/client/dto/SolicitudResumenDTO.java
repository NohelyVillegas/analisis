package com.banquito.analisis.client.dto;

import java.math.BigDecimal;

public class SolicitudResumenDTO {
    private Long idSolicitud;
    private BigDecimal scoreInterno;
    // Agrega otros campos si los necesitas

    public Long getIdSolicitud() {
        return idSolicitud;
    }
    public void setIdSolicitud(Long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }
    public BigDecimal getScoreInterno() {
        return scoreInterno;
    }
    public void setScoreInterno(BigDecimal scoreInterno) {
        this.scoreInterno = scoreInterno;
    }
} 