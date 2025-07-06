package com.banquito.analisis.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConsultasBuroRequest {
    
    private Long idSolicitud;
    private String numeroIdentificacion;
    private String tipoIdentificacion;
} 