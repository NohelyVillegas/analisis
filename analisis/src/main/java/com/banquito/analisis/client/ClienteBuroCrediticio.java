package com.banquito.analisis.client;

import com.banquito.analisis.client.dto.ConsultasBuroRequest;
import com.banquito.analisis.client.dto.ConsultasBuroResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "buro-credit-client", url = "${buro.credit.url:http://localhost:8081}")
public interface ClienteBuroCrediticio {
    
    @PostMapping("/api/v1/buro/consulta")
    ConsultasBuroResponse consultarBuro(@RequestBody ConsultasBuroRequest request);
} 