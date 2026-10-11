
package ProyectoFinal.repository;

import ProyectoFinal.model.UsuarioEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioEmpresaRepository
        extends JpaRepository<UsuarioEmpresa, Long> {
                List<UsuarioEmpresa> findByUsuarioId(Long usuarioId);
                void deleteByEmpresaId(Long empresaId);
}
  
