package ProyectoFinal.repository;

import ProyectoFinal.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrabajadorRepository
        extends JpaRepository<Trabajador, Long> {

    List<Trabajador> findByEmpresaId(Long empresaId);

    List<Trabajador> findByEmpresaIdAndActivoTrue(Long empresaId);

    Optional<Trabajador> findByIdAndEmpresaId(Long id, Long empresaId);

    void deleteByEmpresaId(Long empresaId);
}
