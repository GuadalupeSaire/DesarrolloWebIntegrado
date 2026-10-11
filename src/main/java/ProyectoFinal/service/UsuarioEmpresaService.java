package ProyectoFinal.service;

import ProyectoFinal.model.UsuarioEmpresa;
import ProyectoFinal.repository.UsuarioEmpresaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class UsuarioEmpresaService {
    
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    public UsuarioEmpresaService(UsuarioEmpresaRepository usuarioEmpresaRepository) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
    }

    public List<UsuarioEmpresa> getAllUsuarioEmpresas() {
        return usuarioEmpresaRepository.findAll();
    }

    public Optional<UsuarioEmpresa> getUsuarioEmpresaById(Long id) {
        return usuarioEmpresaRepository.findById(id);
    }

    public UsuarioEmpresa saveUsuarioEmpresa(UsuarioEmpresa usuarioEmpresa) {
        return usuarioEmpresaRepository.save(usuarioEmpresa);
    }

    public void deleteUsuarioEmpresa(Long id) {
        usuarioEmpresaRepository.deleteById(id);
    }
    
    public List<UsuarioEmpresa> getEmpresasByUsuarioId(Long usuarioId) {
    return usuarioEmpresaRepository.findByUsuarioId(usuarioId);
    }
}
