package com.banquito.analisis.repository;

import com.banquito.analisis.model.EvaluacionCrediticia;
import com.banquito.analisis.model.Enums.ResultadoAutomatico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionCrediticiaRepository extends JpaRepository<EvaluacionCrediticia, Long> {
    
    Optional<EvaluacionCrediticia> findByIdSolicitud(Long idSolicitud);
    
    List<EvaluacionCrediticia> findByResultadoAutomatico(ResultadoAutomatico resultadoAutomatico);
    
    Optional<EvaluacionCrediticia> findByIdInformeBuro(Long idInformeBuro);
}