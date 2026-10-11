package ProyectoFinal.service;

import ProyectoFinal.model.Vacante;
import ProyectoFinal.repository.VacanteRepository;
import ProyectoFinal.repository.PostulacionRepository;
import ProyectoFinal.repository.EtapaPostulacionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VacanteService {
    
    private final VacanteRepository vacanteRepository;
    private final PostulacionRepository postulacionRepository;
    private final EtapaPostulacionRepository etapaPostulacionRepository;

    public VacanteService(
            VacanteRepository vacanteRepository,
            PostulacionRepository postulacionRepository,
            EtapaPostulacionRepository etapaPostulacionRepository) {
        this.vacanteRepository = vacanteRepository;
        this.postulacionRepository = postulacionRepository;
        this.etapaPostulacionRepository = etapaPostulacionRepository;
    }

    public List<Vacante> getAllVacantes() {
        return vacanteRepository.findAll();
    }

    public Optional<Vacante> getVacanteById(Long id) {
        return vacanteRepository.findById(id);
    }

    public Vacante saveVacante(Vacante vacante) {
        return vacanteRepository.save(vacante);
    }

    @Transactional
    public void deleteVacante(Long id) {
        if (!vacanteRepository.existsById(id)) {
            return;
        }
        etapaPostulacionRepository.deleteByPostulacionVacanteId(id);
        postulacionRepository.deleteByVacanteId(id);
        vacanteRepository.deleteById(id);
    }
    
    public List<Vacante> getVacantesByEmpresaId(Long empresaId) {
    return vacanteRepository.findByEmpresaId(empresaId);
    }
}
