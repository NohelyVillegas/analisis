package com.espe.analisis.crediticio.client;

import com.espe.analisis.crediticio.client.dto.BureauConsultaRequest;
import com.espe.analisis.crediticio.client.dto.BureauConsultaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bureau-credit-client", url = "${bureau.credit.url:http://localhost:8081}")
public interface BureauCreditClient {
    
    @PostMapping("/api/v1/bureau/consulta")
    BureauConsultaResponse consultarBuro(@RequestBody BureauConsultaRequest request);
}