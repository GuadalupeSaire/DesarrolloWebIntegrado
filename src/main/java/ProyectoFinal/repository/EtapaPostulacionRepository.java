
package ProyectoFinal.repository;

import ProyectoFinal.model.EtapaPostulacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtapaPostulacionRepository
        extends JpaRepository<EtapaPostulacion, Long> {
    void deleteByPostulacionVacanteEmpresaId(Long empresaId);
    void deleteByPostulacionVacanteId(Long vacanteId);
    void deleteByPostulacionCandidatoId(Long candidatoId);
    void deleteByPostulacionId(Long postulacionId);
}
  
