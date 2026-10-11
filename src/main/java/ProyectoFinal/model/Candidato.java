package ProyectoFinal.model;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity @Table(name="candidatos",uniqueConstraints=@UniqueConstraint(columnNames="documento_numero"))
@Getter @Setter @NoArgsConstructor
public class Candidato {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="nombres",nullable=false,length=100) private String nombres;
 @Column(name="apellidos",nullable=false,length=100) private String apellidos;
 @Column(name="tipo_documento",length=10) private String tipoDocumento;
 @Column(name="documento_numero",length=20) private String documentoNumero;
 @Column(length=150) private String correo;
 @Column(length=20) private String telefono;
 @Column(columnDefinition="text") private String formacion;
 @Column(name="experiencia_anios") private Integer experienciaAnios;
 @Column(columnDefinition="text") private String habilidades;
 @Column(name="archivo_cv",length=500) private String archivoCv;
 @Column(name="texto_cv",columnDefinition="text") private String textoCv;
 @Column(name="creado_en",nullable=false) private LocalDateTime creadoEn=LocalDateTime.now();
}
