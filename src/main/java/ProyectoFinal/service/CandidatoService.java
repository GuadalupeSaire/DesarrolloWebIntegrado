package ProyectoFinal.service;

import ProyectoFinal.model.Candidato;
import ProyectoFinal.repository.CandidatoRepository;
import ProyectoFinal.repository.EtapaPostulacionRepository;
import ProyectoFinal.repository.PostulacionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {
    
    private final CandidatoRepository candidatoRepository;
    private final PostulacionRepository postulacionRepository;
    private final EtapaPostulacionRepository etapaPostulacionRepository;

    public CandidatoService(
            CandidatoRepository candidatoRepository,
            PostulacionRepository postulacionRepository,
            EtapaPostulacionRepository etapaPostulacionRepository) {
        this.candidatoRepository = candidatoRepository;
        this.postulacionRepository = postulacionRepository;
        this.etapaPostulacionRepository = etapaPostulacionRepository;
    }

    public List<Candidato> getAllCandidatos() {
        return candidatoRepository.findAll();
    }

    public Optional<Candidato> getCandidatoById(Long id) {
        return candidatoRepository.findById(id);
    }

    public Candidato saveCandidato(Candidato candidato) {
        return candidatoRepository.save(candidato);
    }

    @Transactional
    public void deleteCandidato(Long id) {
        if (!candidatoRepository.existsById(id)) {
            return;
        }
        etapaPostulacionRepository.deleteByPostulacionCandidatoId(id);
        postulacionRepository.deleteByCandidatoId(id);
        candidatoRepository.deleteById(id);
    }
       
}
