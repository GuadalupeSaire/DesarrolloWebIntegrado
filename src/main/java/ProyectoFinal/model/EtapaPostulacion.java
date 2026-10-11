package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity @Table(name="etapas_postulacion")
@Getter @Setter @NoArgsConstructor
public class EtapaPostulacion {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="postulacion_id",nullable=false) private Postulacion postulacion;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="usuario_id") private Usuario usuario;
 @Column(nullable=false,length=60) private String etapa;
 @Column(name="fecha_inicio",nullable=false) private LocalDateTime fechaInicio=LocalDateTime.now();
 @Column(name="fecha_fin") private LocalDateTime fechaFin;
 @Column(columnDefinition="text") private String comentario;
}
