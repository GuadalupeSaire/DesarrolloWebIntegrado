package ProyectoFinal.service;

import ProyectoFinal.model.Trabajador;
import ProyectoFinal.repository.TrabajadorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service 
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;

    public TrabajadorService(TrabajadorRepository trabajadorRepository) {
        this.trabajadorRepository = trabajadorRepository;
    }

    public List<Trabajador> getAllTrabajadores() {
        return trabajadorRepository.findAll();
    }

    public Optional<Trabajador> getTrabajadorById(Long id) {
        return trabajadorRepository.findById(id);
    }

    public long contarTrabajadores(){
        return trabajadorRepository.count();
    }

    public Trabajador saveTrabajador(Trabajador trabajador) {
        return trabajadorRepository.save(trabajador);
    }

    @Transactional
    public void deleteTrabajador(Long id) {
        trabajadorRepository.findById(id).ifPresent(trabajador -> {
            trabajador.setActivo(false);
            trabajadorRepository.save(trabajador);
        });
    }

    public List<Trabajador> getTrabajadoresByEmpresaId(Long empresaId) {
    return trabajadorRepository.findByEmpresaId(empresaId);
    }

    public List<Trabajador> getTrabajadoresActivosByEmpresaId(Long empresaId) {
    return trabajadorRepository.findByEmpresaIdAndActivoTrue(empresaId);
    }

    public Optional<Trabajador> getTrabajadorByIdAndEmpresaId(
        Long trabajadorId,
        Long empresaId){
            return trabajadorRepository.findByIdAndEmpresaId(trabajadorId, empresaId);
        }
    
}
