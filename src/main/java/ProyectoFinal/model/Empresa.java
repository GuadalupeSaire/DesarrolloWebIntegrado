package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false, length = 11, unique = true)
    private String ruc;

    public String getRuc() {
        return ruc;
    }
    @Column(length = 200)
    private String direccion;

    @Column(length = 9)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @OneToMany(mappedBy = "empresa")
    private java.util.List<Contacto> contactos = new java.util.ArrayList<>();
    
    @OneToMany(mappedBy = "empresa")
    private java.util.List<UsuarioEmpresa> asignaciones = new java.util.ArrayList<>();
}
