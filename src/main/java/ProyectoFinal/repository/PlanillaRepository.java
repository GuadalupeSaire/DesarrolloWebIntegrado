package ProyectoFinal.repository;

import ProyectoFinal.model.Planilla;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanillaRepository
        extends JpaRepository<Planilla, Long> {

    Optional<Planilla> findByEmpresaIdAndPeriodoAnioAndPeriodoMes(
            Long empresaId,
            Integer periodoAnio,
            Integer periodoMes
    );

    List<Planilla> findByEmpresaId(Long empresaId);

    Optional<Planilla> findByIdAndEmpresaId(Long id, Long empresaId);

    @Query("""
        SELECT p
        FROM Planilla p
        JOIN FETCH p.empresa
        WHERE p.id = :id
    """)
    Optional<Planilla> findByIdWithEmpresa(
            @Param("id") Long id
    );

    void deleteByEmpresaId(Long empresaId);
}
