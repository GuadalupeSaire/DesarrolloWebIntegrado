package ProyectoFinal.service;

import ProyectoFinal.model.Postulacion;
import ProyectoFinal.repository.PostulacionRepository;
import ProyectoFinal.repository.EtapaPostulacionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostulacionService {
    
    private final PostulacionRepository postulacionRepository;
    private final EtapaPostulacionRepository etapaPostulacionRepository;

    public PostulacionService(
            PostulacionRepository postulacionRepository,
            EtapaPostulacionRepository etapaPostulacionRepository) {
        this.postulacionRepository = postulacionRepository;
        this.etapaPostulacionRepository = etapaPostulacionRepository;
    }

    public List<Postulacion> getAllPostulaciones() {
        return postulacionRepository.findAll();
    }

    public Optional<Postulacion> getPostulacionById(Long id) {
        return postulacionRepository.findById(id);
    }

    public Postulacion savePostulacion(Postulacion postulacion) {
        return postulacionRepository.save(postulacion);
    }

    @Transactional
    public void deletePostulacion(Long id) {
        if (!postulacionRepository.existsById(id)) {
            return;
        }
        etapaPostulacionRepository.deleteByPostulacionId(id);
        postulacionRepository.deleteById(id);
    }
    
    public List<Postulacion> getPostulacionesByVacanteId(Long vacanteId) {
    return postulacionRepository.findByVacanteId(vacanteId);
    }
}
