package ProyectoFinal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="roles")
@Getter @Setter @NoArgsConstructor
public class Rol {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=60) private String nombre;
 @Column(length=250) private String descripcion;
 @OneToMany(mappedBy="rol") private java.util.List<UsuarioRol> usuarios=new java.util.ArrayList<>();
}
