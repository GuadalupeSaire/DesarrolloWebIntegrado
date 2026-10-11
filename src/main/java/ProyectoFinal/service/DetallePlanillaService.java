package ProyectoFinal.service;

import ProyectoFinal.model.DetallePlanilla;
import ProyectoFinal.repository.DetallePlanillaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePlanillaService {

    private final DetallePlanillaRepository detallePlanillaRepository;

    public DetallePlanillaService(
            DetallePlanillaRepository detallePlanillaRepository) {
        this.detallePlanillaRepository = detallePlanillaRepository;
    }

    public List<DetallePlanilla> getAllDetallePlanillas() {
        return detallePlanillaRepository.findAll();
    }

    public Optional<DetallePlanilla> getDetallePlanillaById(Long id) {
        return detallePlanillaRepository.findById(id);
    }

    public DetallePlanilla saveDetallePlanilla(DetallePlanilla detalle) {
        return detallePlanillaRepository.save(detalle);
    }

    public void deleteDetallePlanilla(Long id) {
        detallePlanillaRepository.deleteById(id);
    }

    public List<DetallePlanilla> getDetallesByPlanillaId(Long planillaId) {
    return detallePlanillaRepository.findByPlanillaId(planillaId);
    }
}