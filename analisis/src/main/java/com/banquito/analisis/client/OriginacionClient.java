package com.banquito.analisis.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.banquito.analisis.client.dto.SolicitudResumenDTO;

@FeignClient(name = "originacion-client", url = "${originacion.url:http://localhost:8082}")
public interface OriginacionClient {
    @GetMapping("/api/v1/solicitudes/{idSolicitud}/resumen")
    SolicitudResumenDTO obtenerResumenSolicitud(@PathVariable("idSolicitud") Long idSolicitud);
} 