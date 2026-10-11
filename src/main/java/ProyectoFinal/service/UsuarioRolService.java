package ProyectoFinal.service;

import ProyectoFinal.model.UsuarioRol;
import ProyectoFinal.repository.UsuarioRolRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class UsuarioRolService {
    
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioRolService(UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<UsuarioRol> getAllUsuarioRoles() {
        return usuarioRolRepository.findAll();
    }

    public Optional<UsuarioRol> getUsuarioRolById(Long id) {
        return usuarioRolRepository.findById(id);
    }

    public UsuarioRol saveUsuarioRol(UsuarioRol usuarioRol) {
        return usuarioRolRepository.save(usuarioRol);
    }

    public void deleteUsuarioRol(Long id) {
        usuarioRolRepository.deleteById(id);
    }

    public List<UsuarioRol> getRolesByUsuarioId(Long usuarioId) {
    return usuarioRolRepository.findByUsuarioId(usuarioId);
    }
    
}
