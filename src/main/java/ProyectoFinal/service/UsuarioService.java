package ProyectoFinal.service;

import ProyectoFinal.model.Usuario;
import ProyectoFinal.model.Rol;
import ProyectoFinal.model.UsuarioRol;

import ProyectoFinal.repository.UsuarioRepository;
import ProyectoFinal.repository.RolRepository;
import ProyectoFinal.repository.UsuarioRolRepository;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.model.UsuarioEmpresa;
import ProyectoFinal.repository.EmpresaRepository;
import ProyectoFinal.repository.UsuarioEmpresaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder,
            EmpresaRepository empresaRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepository = empresaRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Optional<Long> obtenerEmpresaActivaIdPorCorreo(String correo){
        return  usuarioRepository.findByCorreo(correo)
        .flatMap(usuario -> 
            obtenerEmpresasAsignadasId(usuario.getId()));
    }

    public Optional<Long> obtenerRolAsignadoId(Long usuarioId) {
    return usuarioRolRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(asignacion -> asignacion.getRol().getId())
            .findFirst();
    }

    public Optional<Long> obtenerEmpresasAsignadasId(Long usuarioId){
        return  usuarioEmpresaRepository.findByUsuarioId(usuarioId)
        .stream()
        .filter(asignacion -> Boolean.TRUE.equals(asignacion.getEstado()))
        .map(asignacion -> asignacion.getEmpresa().getId())
        .findFirst();
    }

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    @Transactional
    public Usuario guardarUsuario(
            Usuario usuario,
            String password,
            Long rolId,
            Long empresaId) {

        /*
         * Si es un usuario NUEVO,
         * la contraseña es obligatoria.
         */
        if (usuario.getId() == null) {

            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException(
                        "La contraseña es obligatoria para un nuevo usuario.");
            }

            usuario.setPasswordHash(
                    passwordEncoder.encode(password)
            );

        } else {

            /*
             * Si estamos EDITANDO y no escribió
             * una nueva contraseña, conservamos
             * la contraseña anterior.
             */
            Usuario usuarioActual = usuarioRepository
                    .findById(usuario.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Usuario no encontrado: " + usuario.getId()));

            if (password != null && !password.isBlank()) {

                usuario.setPasswordHash(
                        passwordEncoder.encode(password)
                );

            } else {

                usuario.setPasswordHash(
                        usuarioActual.getPasswordHash()
                );
            }
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        /*
         * Si se seleccionó un rol,
         * creamos la relación UsuarioRol.
         */
        if (rolId != null) {
            asignarRol(usuarioGuardado.getId(), rolId);
        }

        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> 
                new IllegalArgumentException(
                    "Empresa no encontrada: " + empresaId));
        
        List <UsuarioEmpresa> asignaciones = 
                    usuarioEmpresaRepository.findByUsuarioId(usuarioGuardado.getId());
        
        UsuarioEmpresa asignacion;
        if (asignaciones.isEmpty()) {
            asignacion = new UsuarioEmpresa();
        } else{
            asignacion = asignaciones.get(0);
        }

        asignacion.setUsuario(usuarioGuardado);
        asignacion.setEmpresa(empresa);
        asignacion.setEstado(true);

        usuarioEmpresaRepository.save(asignacion);

        return usuarioGuardado;
    }

    @Transactional
    public void asignarRol(Long usuarioId, Long rolId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado: " + usuarioId));

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Rol no encontrado: " + rolId));

        List<UsuarioRol> asignaciones =
        usuarioRolRepository.findByUsuarioId(usuarioId);

        if (!asignaciones.isEmpty()) {
            UsuarioRol asignacion = asignaciones.get(0);
            asignacion.setRol(rol);
            usuarioRolRepository.save(asignacion);
            return;
        }

        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setUsuario(usuario);
        asignacion.setRol(rol);
        usuarioRolRepository.save(asignacion);

    }

    @Transactional
    public void deleteUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setEstado(false);
            usuarioRepository.save(usuario);
        });
    }
}
