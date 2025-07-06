package com.espe.analisis.crediticio.repository;

import com.espe.analisis.crediticio.model.InformesBuro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformesBuroRepository extends JpaRepository<InformesBuro, Long> {
    
    Optional<InformesBuro> findByIdConsultaBuro(Long idConsultaBuro);
}