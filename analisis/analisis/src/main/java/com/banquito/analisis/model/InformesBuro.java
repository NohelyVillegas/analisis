package com.espe.analisis.crediticio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "informes_buro", schema = "analisis_crediticio")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class InformesBuro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe_buro")
    private Long idInformeBuro;
    
    @Column(name = "capacidad_pago_reportada", precision = 10, scale = 2)
    private BigDecimal capacidadPagoReportada;
    
    @Column(name = "id_consulta_buro")
    private Long idConsultaBuro;
    
    @Column(name = "json_respuesta_completa", columnDefinition = "jsonb")
    private String jsonRespuestaCompleta;
    
    @Column(name = "monto_total_adeudado", precision = 12, scale = 2)
    private BigDecimal montoTotalAdeudado;
    
    @Column(name = "numero_deudas_impagas")
    private Integer numeroDeudasImpagas;
    
    @Column(name = "score", precision = 4, scale = 0)
    private BigDecimal score;
    
    @Version
    private Integer version;
    
    public InformesBuro(Long idInformeBuro) {
        this.idInformeBuro = idInformeBuro;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InformesBuro that)) return false;
        return Objects.equals(idInformeBuro, that.idInformeBuro);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(idInformeBuro);
    }
}