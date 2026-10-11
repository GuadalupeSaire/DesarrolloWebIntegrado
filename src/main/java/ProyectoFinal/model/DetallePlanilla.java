package ProyectoFinal.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity @Table(name="detalles_planilla",uniqueConstraints=@UniqueConstraint(columnNames={"planilla_id","trabajador_id"}))
@Getter @Setter @NoArgsConstructor
public class DetallePlanilla {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="planilla_id",nullable=false) private Planilla planilla;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="trabajador_id",nullable=false) private Trabajador trabajador;
 @Column(name="dias_laborados") private Integer diasLaborados;
 @Column(name="faltas") private Integer faltas=0;
 @Column(name="horas_extra",precision=8,scale=2) private BigDecimal horasExtra=BigDecimal.ZERO;
 @Column(name="sueldo_base",precision=12,scale=2) private BigDecimal sueldoBase=BigDecimal.ZERO;
 @Column(name="bonificaciones",precision=12,scale=2) private BigDecimal bonificaciones=BigDecimal.ZERO;
 @Column(name="descuentos",precision=12,scale=2) private BigDecimal descuentos=BigDecimal.ZERO;
 @Column(name="aporte_pension",precision=12,scale=2) private BigDecimal aportePension=BigDecimal.ZERO;
 @Column(name="retencion_quinta",precision=12,scale=2) private BigDecimal retencionQuinta=BigDecimal.ZERO;
 @Column(name="essalud_empleador",precision=12,scale=2) private BigDecimal essaludEmpleador=BigDecimal.ZERO;
 @Column(name="subsidios",precision=12,scale=2) private BigDecimal subsidios=BigDecimal.ZERO;
 @Column(name="horas_extra_importe",precision=12,scale=2) private BigDecimal importeHorasExtra=BigDecimal.ZERO;
 @Column(name="total_bruto",precision=12,scale=2) private BigDecimal totalBruto=BigDecimal.ZERO;
 @Column(name="total_neto",precision=12,scale=2) private BigDecimal totalNeto=BigDecimal.ZERO;
}
