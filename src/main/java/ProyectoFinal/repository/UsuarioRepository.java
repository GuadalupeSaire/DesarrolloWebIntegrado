package ProyectoFinal.repository;

import ProyectoFinal.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    @Query("""
        SELECT DISTINCT u
        FROM Usuario u
        LEFT JOIN FETCH u.roles ur
        LEFT JOIN FETCH ur.rol
        WHERE u.correo = :correo
    """)
    Optional<Usuario> findByCorreoWithRoles(@Param("correo") String correo);

    @Query("""
        SELECT DISTINCT u
        FROM Usuario u
        LEFT JOIN FETCH u.roles ur
        LEFT JOIN FETCH ur.rol
    """)
    List<Usuario> findAllWithRoles();
}