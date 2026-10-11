package ProyectoFinal.repository;

import ProyectoFinal.model.DetallePlanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetallePlanillaRepository
        extends JpaRepository<DetallePlanilla, Long> {

    List<DetallePlanilla> findByPlanillaId(Long planillaId);

    void deleteByPlanillaId(Long planillaId);

    @Query("""
        SELECT d
        FROM DetallePlanilla d
        JOIN FETCH d.trabajador
        WHERE d.planilla.id = :planillaId
        ORDER BY d.trabajador.apellidos, d.trabajador.nombres
    """)
    List<DetallePlanilla> findByPlanillaIdWithTrabajador(
            @Param("planillaId") Long planillaId
    );

    void deleteByPlanillaEmpresaId(Long empresaId);

    void deleteByTrabajadorId(Long trabajadorId);

    void deleteByTrabajadorEmpresaId(Long empresaId);
}
