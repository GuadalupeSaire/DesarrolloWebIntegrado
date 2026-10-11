package ProyectoFinal.service;

import ProyectoFinal.model.Rol;
import ProyectoFinal.repository.RolRepository;
import ProyectoFinal.repository.UsuarioRolRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class RolService {
    
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public RolService(
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    public Optional<Rol> getRolById(Long id) {
        return rolRepository.findById(id);
    }

    public Rol saveRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public boolean deleteRol(Long id) {
        if (!rolRepository.existsById(id) || usuarioRolRepository.existsByRolId(id)) {
            return false;
        }
        rolRepository.deleteById(id);
        return true;
    }
    
}
