package ProyectoFinal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity @Table(name="contactos")
@Getter @Setter @NoArgsConstructor
public class Contacto {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="empresa_id",nullable=false) private Empresa empresa;
 @NotBlank(message="El nombre del contacto es obligatorio.")
 @Column(nullable=false,length=120) private String nombre;
 @Column(length=100) private String cargo;
 @Email(message="Ingresa un correo válido.")
 @Column(length=150) private String correo;
 @Column(length=20) private String telefono;
 @Column(nullable=false) private Boolean principal=false;
}
