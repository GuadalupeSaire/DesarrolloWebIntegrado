package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import lombok.*;

@Entity @Table(name="planillas",uniqueConstraints=@UniqueConstraint(columnNames={"empresa_id","periodo_anio","periodo_mes"}))
@Getter @Setter @NoArgsConstructor
public class Planilla {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="empresa_id",nullable=false) private Empresa empresa;
 @Column(name="periodo_anio",nullable=false) private Integer periodoAnio;
 @Column(name="periodo_mes",nullable=false) private Integer periodoMes;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="procesada_por") private Usuario procesadaPor;
 @Column(name="fecha_proceso") private LocalDateTime fechaProceso;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoPlanilla estado=EstadoPlanilla.BORRADOR;
 @Column(name="total_bruto",precision=14,scale=2) private BigDecimal totalBruto=BigDecimal.ZERO;
 @Column(name="total_descuentos",precision=14,scale=2) private BigDecimal totalDescuentos=BigDecimal.ZERO;
 @Column(name="total_neto",precision=14,scale=2) private BigDecimal totalNeto=BigDecimal.ZERO;
 public enum EstadoPlanilla { BORRADOR, CALCULADA, CERRADA }
}
