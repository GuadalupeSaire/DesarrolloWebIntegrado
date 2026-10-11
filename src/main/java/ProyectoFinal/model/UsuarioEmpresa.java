package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "usuario_empresas", uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "empresa_id" }))
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEmpresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion = LocalDate.now();
    @Column(nullable = false)
    private Boolean estado = true;
}
