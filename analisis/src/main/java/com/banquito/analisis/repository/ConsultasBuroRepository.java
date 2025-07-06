package com.banquito.analisis.repository;

import com.banquito.analisis.model.ConsultasBuro;
import com.banquito.analisis.model.Enums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultasBuroRepository extends JpaRepository<ConsultasBuro, Long> {
    
    Optional<ConsultasBuro> findByIdSolicitud(Long idSolicitud);
    
    List<ConsultasBuro> findByEstadoConsulta(Enums.EstadoConsulta estadoConsulta);
    
    List<ConsultasBuro> findByIdSolicitudAndEstadoConsulta(Long idSolicitud, Enums.EstadoConsulta estadoConsulta);
}