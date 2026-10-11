package ProyectoFinal.repository;

import ProyectoFinal.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRolRepository
        extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioId(Long usuarioId);
    boolean existsByRolId(Long rolId);
}
