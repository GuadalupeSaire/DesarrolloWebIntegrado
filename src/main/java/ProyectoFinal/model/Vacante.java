package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import lombok.*;

@Entity @Table(name="vacantes")
@Getter @Setter @NoArgsConstructor
public class Vacante {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="empresa_id",nullable=false) private Empresa empresa;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="creado_por") private Usuario creadoPor;
 @Column(nullable=false,length=150) private String titulo;
 @Column(columnDefinition="text") private String descripcion;
 @Column(columnDefinition="text") private String requisitos;
 @Column(name="nivel_educativo",length=100) private String nivelEducativo;
 @Column(columnDefinition="text") private String competencias;
 @Column(name="experiencia_anios") private Integer experienciaAnios;
 @Column(name="palabras_clave",columnDefinition="text") private String palabrasClave;
 @Column(name="cantidad_puestos") private Integer cantidadPuestos=1;
 @Column(precision=12,scale=2) private BigDecimal salario;
 @Column(name="fecha_publicacion") private LocalDate fechaPublicacion;
 @Column(name="fecha_cierre") private LocalDate fechaCierre;
 @Column(name="sla_dias") private Integer slaDias;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoVacante estado=EstadoVacante.ABIERTA;
 @Enumerated(EnumType.STRING) @Column(name="estado_terna",length=25,columnDefinition="varchar(25) not null default 'SIN_TERNA'") private EstadoTerna estadoTerna=EstadoTerna.SIN_TERNA;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="terna_aprobada_por") private Usuario ternaAprobadaPor;
 @Column(name="fecha_terna") private LocalDateTime fechaTerna;
 public enum EstadoTerna { SIN_TERNA, PENDIENTE_APROBACION, APROBADA, RECHAZADA }
 public enum EstadoVacante { ABIERTA, CERRADA, CANCELADA }
}
