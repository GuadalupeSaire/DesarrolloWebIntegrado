package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity @Table(name="usuarios")
@Getter @Setter @NoArgsConstructor
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=80) private String nombres;
 @Column(nullable=false,length=80) private String apellidos;
 @Column(nullable=false,unique=true,length=150) private String correo;
 @Column(name="password_hash",nullable=false,length=255) private String passwordHash;
 @Column(nullable=false) private Boolean estado=true;
 @Column(name="creado_en",nullable=false) private LocalDateTime creadoEn=LocalDateTime.now();
 @OneToMany(mappedBy="usuario") private java.util.List<UsuarioRol> roles=new java.util.ArrayList<>();
 @OneToMany(mappedBy="usuario") private java.util.List<UsuarioEmpresa> empresas=new java.util.ArrayList<>();
}
