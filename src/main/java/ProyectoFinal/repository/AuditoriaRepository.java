
package ProyectoFinal.repository;

import ProyectoFinal.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long> {
    @Modifying
    @Query("UPDATE Auditoria a SET a.empresa = null WHERE a.empresa.id = :empresaId")
    int desvincularEmpresa(@Param("empresaId") Long empresaId);
}
  
