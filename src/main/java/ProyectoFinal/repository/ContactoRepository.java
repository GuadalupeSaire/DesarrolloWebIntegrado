
package ProyectoFinal.repository;

import ProyectoFinal.model.Contacto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactoRepository
        extends JpaRepository<Contacto, Long> {
                @EntityGraph(attributePaths = "empresa")
                @Query("""
                    SELECT c FROM Contacto c
                    JOIN c.empresa e
                    WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
                       OR LOWER(COALESCE(c.cargo, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                       OR LOWER(COALESCE(c.correo, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                       OR LOWER(COALESCE(c.telefono, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                       OR LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))
                    ORDER BY c.principal DESC, c.nombre ASC
                    """)
                List<Contacto> buscarTodos(@Param("termino") String termino);

                @EntityGraph(attributePaths = "empresa")
                @Query("""
                    SELECT c FROM Contacto c
                    JOIN c.empresa e
                    WHERE e.id = :empresaId
                      AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
                           OR LOWER(COALESCE(c.cargo, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                           OR LOWER(COALESCE(c.correo, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                           OR LOWER(COALESCE(c.telefono, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                           OR LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%')))
                    ORDER BY c.principal DESC, c.nombre ASC
                    """)
                List<Contacto> buscarPorEmpresa(@Param("empresaId") Long empresaId,
                                                @Param("termino") String termino);
                List<Contacto> findByEmpresaId(Long empresaId);
                void deleteByEmpresaId(Long empresaId);
}
  
