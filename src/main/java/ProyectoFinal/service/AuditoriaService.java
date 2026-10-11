package ProyectoFinal.service;

import ProyectoFinal.model.Auditoria;
import ProyectoFinal.repository.AuditoriaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public List<Auditoria> getAllAuditorias() {
        return auditoriaRepository.findAll();
    }

    public Optional<Auditoria> getAuditoriaById(Long id) {
        return auditoriaRepository.findById(id);
    }

    public Auditoria saveAuditoria(Auditoria auditoria) {
        return auditoriaRepository.save(auditoria);
    }

    public void deleteAuditoria(Long id) {
        auditoriaRepository.deleteById(id);
    }
}