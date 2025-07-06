package com.espe.analisis.crediticio.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BureauConsultaRequest {
    
    private Long idSolicitud;
    private String numeroIdentificacion;
    private String tipoIdentificacion;
}