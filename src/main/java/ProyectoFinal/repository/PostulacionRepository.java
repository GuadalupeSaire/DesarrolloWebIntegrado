
package ProyectoFinal.repository;

import ProyectoFinal.model.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostulacionRepository
        extends JpaRepository<Postulacion, Long> {
                List<Postulacion> findByVacanteId(Long vacanteId);
                void deleteByVacanteEmpresaId(Long empresaId);
                void deleteByVacanteId(Long vacanteId);
                void deleteByCandidatoId(Long candidatoId);
}
  
