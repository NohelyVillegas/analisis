package com.espe.analisis.crediticio.model;

import com.espe.analisis.crediticio.model.DecisionManual;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "observacion_analista", schema = "analisis_crediticio")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ObservacionAnalista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_observacion")
    private Long idObservacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "decision_manual")
    private DecisionManual decisionManual;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;
    
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @Column(name = "justificacion", columnDefinition = "text")
    private String justificacion;
    
    @Version
    private Integer version;
    
    public ObservacionAnalista(Long idObservacion) {
        this.idObservacion = idObservacion;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObservacionAnalista that)) return false;
        return Objects.equals(idObservacion, that.idObservacion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(idObservacion);
    }
}