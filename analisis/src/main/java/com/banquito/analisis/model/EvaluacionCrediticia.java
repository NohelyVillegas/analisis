package com.espe.analisis.crediticio.model;

import com.espe.analisis.crediticio.model.DecisionFinalAnalista;
import com.espe.analisis.crediticio.model.ResultadoAutomatico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "evaluacion_crediticia", schema = "analisis_crediticio")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EvaluacionCrediticia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "decision_final_analista")
    private DecisionFinalAnalista decisionFinalAnalista;
    
    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;
    
    @Column(name = "id_informe_buro")
    private Long idInformeBuro;
    
    @Column(name = "id_solicitud")
    private Long idSolicitud;
    
    @Column(name = "justificacion_analista", columnDefinition = "text")
    private String justificacionAnalista;
    
    @Column(name = "observaciones_motor_reglas", columnDefinition = "text")
    private String observacionesMotorReglas;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_automatico")
    private ResultadoAutomatico resultadoAutomatico;
    
    @Column(name = "score_interno_calculado", precision = 4, scale = 0)
    private BigDecimal scoreInternoCalculado;
    
    @Version
    private Integer version;
    
    public EvaluacionCrediticia(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluacionCrediticia that)) return false;
        return Objects.equals(idEvaluacion, that.idEvaluacion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(idEvaluacion);
    }
}