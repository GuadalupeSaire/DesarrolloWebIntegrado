package ProyectoFinal.service;

import ProyectoFinal.model.EtapaPostulacion;
import ProyectoFinal.repository.EtapaPostulacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtapaPostulacionService {

    private final EtapaPostulacionRepository etapaPostulacionRepository;

    public EtapaPostulacionService(
            EtapaPostulacionRepository etapaPostulacionRepository) {
        this.etapaPostulacionRepository = etapaPostulacionRepository;
    }

    public List<EtapaPostulacion> getAllEtapas() {
        return etapaPostulacionRepository.findAll();
    }

    public Optional<EtapaPostulacion> getEtapaById(Long id) {
        return etapaPostulacionRepository.findById(id);
    }

    public EtapaPostulacion saveEtapa(EtapaPostulacion etapa) {
        return etapaPostulacionRepository.save(etapa);
    }

    public void deleteEtapa(Long id) {
        etapaPostulacionRepository.deleteById(id);
    }
}