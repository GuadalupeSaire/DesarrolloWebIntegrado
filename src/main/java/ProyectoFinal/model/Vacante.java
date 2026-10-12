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
 @Column(precision=12,scale=2) private BigDecimal salario;
 @Column(name="fecha_publicacion") private LocalDate fechaPublicacion;
 @Column(name="fecha_cierre") private LocalDate fechaCierre;
 @Column(name="sla_dias") private Integer slaDias;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoVacante estado=EstadoVacante.BORRADOR;
 public enum EstadoVacante { BORRADOR, PUBLICADA, CERRADA, CANCELADA }
}
