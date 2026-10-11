package ProyectoFinal.controller;

import ProyectoFinal.model.Usuario;
import ProyectoFinal.model.UsuarioRol;
import ProyectoFinal.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioGlobalControllerAdvice {

    private final UsuarioRepository usuarioRepository;

    public UsuarioGlobalControllerAdvice(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(Authentication authentication) {

        if (authentication == null ||
            !authentication.isAuthenticated()) {

            return null;
        }

        String correo = authentication.getName();

        // Usuario temporal de desarrollo
        if ("user@user.com".equalsIgnoreCase(correo)) {
            return null;
        }

        return usuarioRepository
                .findByCorreoWithRoles(correo)
                .orElse(null);
    }

    @ModelAttribute("rolActual")
    public String rolActual(Authentication authentication) {

        if (authentication == null ||
            !authentication.isAuthenticated()) {

            return "";
        }

        String correo = authentication.getName();

        // Usuario temporal de desarrollo
        if ("user@user.com".equalsIgnoreCase(correo)) {
            return "Administrador";
        }

        Usuario usuario = usuarioRepository
                .findByCorreoWithRoles(correo)
                .orElse(null);

        if (usuario == null ||
            usuario.getRoles() == null ||
            usuario.getRoles().isEmpty()) {

            return "Sin rol";
        }

        return usuario.getRoles()
                .stream()
                .map(UsuarioRol::getRol)
                .map(rol -> rol.getNombre())
                .findFirst()
                .orElse("Sin rol");
    }

    @ModelAttribute("esAdministrador")
    public boolean esAdministrador(Authentication authentication) {
        return tieneAutoridad(authentication, "ROLE_ADMINISTRADOR");
    }

    @ModelAttribute("esPlanillas")
    public boolean esPlanillas(Authentication authentication) {
        return tieneAutoridad(authentication, "ROLE_PLANILLAS");
    }

    @ModelAttribute("esReclutamiento")
    public boolean esReclutamiento(Authentication authentication) {
        return tieneAutoridad(authentication, "ROLE_ADMINISTRADOR")
                || tieneAutoridad(authentication, "ROLE_JEFE_RECLUTAMIENTO")
                || tieneAutoridad(authentication, "ROLE_ANALISTA")
                || tieneAutoridad(authentication, "ROLE_ASISTENTE");
    }

    private boolean tieneAutoridad(Authentication authentication, String autoridad) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(autoridad::equals);
    }
}
