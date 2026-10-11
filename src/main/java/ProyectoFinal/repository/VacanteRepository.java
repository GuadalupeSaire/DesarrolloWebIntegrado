
package ProyectoFinal.repository;

import ProyectoFinal.model.Vacante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacanteRepository
        extends JpaRepository<Vacante, Long> {
                List<Vacante> findByEmpresaId(Long empresaId);
                void deleteByEmpresaId(Long empresaId);
}
  
