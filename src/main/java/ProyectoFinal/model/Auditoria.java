package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity @Table(name="auditorias")
@Getter @Setter @NoArgsConstructor
public class Auditoria {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="usuario_id") private Usuario usuario;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="empresa_id") private Empresa empresa;
 @Column(nullable=false,length=80) private String entidad;
 @Column(name="entidad_id") private Long entidadId;
 @Column(nullable=false,length=40) private String accion;
 @Column(name="fecha_evento",nullable=false) private LocalDateTime fechaEvento=LocalDateTime.now();
 @Column(name="valor_anterior",columnDefinition="text") private String valorAnterior;
 @Column(name="valor_nuevo",columnDefinition="text") private String valorNuevo;
 @Column(name="ip_origen",length=50) private String ipOrigen;
}
