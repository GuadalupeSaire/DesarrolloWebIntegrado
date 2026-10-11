package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity @Table(name="usuario_roles", uniqueConstraints=@UniqueConstraint(columnNames={"usuario_id","rol_id"}))
@Getter @Setter @NoArgsConstructor
public class UsuarioRol {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="rol_id",nullable=false) private Rol rol;
 @Column(name="asignado_en",nullable=false) private LocalDateTime asignadoEn=LocalDateTime.now();
}
