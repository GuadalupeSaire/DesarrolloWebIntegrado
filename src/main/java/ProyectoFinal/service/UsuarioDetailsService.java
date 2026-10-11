package ProyectoFinal.service;

import ProyectoFinal.model.Usuario;
import ProyectoFinal.repository.UsuarioRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        // Cuenta temporal con permisos de administrador para desarrollo.
        if ("user@user.com".equalsIgnoreCase(correo)) {

            return org.springframework.security.core.userdetails.User
                    .withUsername("user@user.com")
                    .password("{noop}Gescont123!")
                    .roles("ADMINISTRADOR")
                    .build();
        }

        // Usuario real de PostgreSQL
        Usuario usuario = usuarioRepository.findByCorreoWithRoles(correo)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + correo
                        ));

        List<SimpleGrantedAuthority> authorities =
                usuario.getRoles()
                        .stream()
                        .map(usuarioRol -> usuarioRol.getRol())
                        .map(rol -> new SimpleGrantedAuthority(
                                "ROLE_" + rol.getNombre().toUpperCase()
                        ))
                        .toList();

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getCorreo())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .disabled(!usuario.getEstado())
                .build();
    }
}
