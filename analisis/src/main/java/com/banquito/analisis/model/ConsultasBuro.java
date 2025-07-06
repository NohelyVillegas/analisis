package com.espe.analisis.crediticio.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "consultas_buro", schema = "analisis_crediticio")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ConsultasBuro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idConsulta;
    
    @Column(name = "cuentas_activas")
    private Integer cuentasActivas;
    
    @Column(name = "cuentas_morosas")
    private Integer cuentasMorosas;
    
    @Column(name = "datos_buro_encriptado")
    private byte[] datosBuroEncriptado;
    
    @Column(name = "dias_mora_promedio", precision = 5, scale = 2)
    private BigDecimal diasMoraPromedio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_consulta")
    private EstadoConsulta estadoConsulta;
    
    @Column(name = "fecha_consulta")
    private LocalDateTime fechaConsulta;
    
    @Column(name = "fecha_primera_mora")
    private LocalDate fechaPrimeraMora;
    
    @Column(name = "id_solicitud")
    private Long idSolicitud;
    
    @Column(name = "monto_moroso_total", precision = 12, scale = 2)
    private BigDecimal montoMorosoTotal;
    
    @Column(name = "score_externo", precision = 5, scale = 2)
    private BigDecimal scoreExterno;
    
    @Version
    private Integer version;
    
    public ConsultasBuro(Long idConsulta) {
        this.idConsulta = idConsulta;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsultasBuro that)) return false;
        return Objects.equals(idConsulta, that.idConsulta);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(idConsulta);
    }
}