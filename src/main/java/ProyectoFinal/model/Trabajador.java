package ProyectoFinal.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.*;
import lombok.*;

@Entity @Table(name="trabajadores",uniqueConstraints=@UniqueConstraint(columnNames={"empresa_id","documento_numero"}))
@Getter @Setter @NoArgsConstructor
public class Trabajador {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="empresa_id",nullable=false) private Empresa empresa;
 @Column(name="nombres",nullable=false,length=100) private String nombres;
 @Column(name="apellidos",nullable=false,length=100) private String apellidos;
 @Column(name="tipo_documento",length=10) private String tipoDocumento;
 @Column(name="documento_numero",nullable=false,length=20) private String documentoNumero;
 @Column(length=150) private String correo;
 @Column(length=20) private String telefono;
 @Column(length=120) private String cargo;
 @Column(name="fecha_ingreso") private LocalDate fechaIngreso;
 @Column(name="fecha_cese") private LocalDate fechaCese;
 @Column(name="sueldo_base", precision=12, scale=2, nullable=false)
private BigDecimal sueldoBase = BigDecimal.ZERO;
 @Column(name="regimen_pensionario", length=10)
 private String regimenPensionario = "ONP";
 @Column(name="tasa_pension", precision=6, scale=4)
 private BigDecimal tasaPension = new BigDecimal("0.1300");
 @Column(nullable=false) private Boolean activo=true;
}
