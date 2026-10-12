package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import lombok.*;

@Entity @Table(name="postulaciones",uniqueConstraints=@UniqueConstraint(columnNames={"vacante_id","candidato_id"}))
@Getter @Setter @NoArgsConstructor
public class Postulacion {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="vacante_id",nullable=false) private Vacante vacante;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="candidato_id",nullable=false) private Candidato candidato;
 @Column(name="puntaje_cv",precision=5,scale=2) private BigDecimal puntajeCv;
 @Column(name="fecha_postulacion",nullable=false) private LocalDateTime fechaPostulacion=LocalDateTime.now();
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=25) private EstadoPostulacion estado=EstadoPostulacion.RECIBIDA;
 @Column(columnDefinition="text") private String observaciones;
 public enum EstadoPostulacion { RECIBIDA, EN_REVISION, PRESELECCIONADA, ENTREVISTA, APROBADA, RECHAZADA, CONTRATADA }
}
