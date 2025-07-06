package com.espe.analisis.crediticio.repository;

import com.espe.analisis.crediticio.model.ConsultasBuro;
import com.espe.analisis.crediticio.model.EstadoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultasBuroRepository extends JpaRepository<ConsultasBuro, Long> {
    
    Optional<ConsultasBuro> findByIdSolicitud(Long idSolicitud);
    
    List<ConsultasBuro> findByEstadoConsulta(EstadoConsulta estadoConsulta);
    
    List<ConsultasBuro> findByIdSolicitudAndEstadoConsulta(Long idSolicitud, EstadoConsulta estadoConsulta);
}