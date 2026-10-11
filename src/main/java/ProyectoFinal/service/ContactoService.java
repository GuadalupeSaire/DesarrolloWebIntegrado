package ProyectoFinal.service;

import  ProyectoFinal.model.Contacto;
import  ProyectoFinal.repository.ContactoRepository;

import org.springframework.stereotype.Service;

import  java.util.List;
import  java.util.Optional;

@Service 
public class ContactoService {
    
    private final ContactoRepository contactoRepository;

    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    public List<Contacto> getAllContactos() {
        return contactoRepository.findAll();
    }

    public List<Contacto> buscar(Long empresaId, String termino) {
        String filtro = termino == null ? "" : termino.trim();
        return empresaId == null
                ? contactoRepository.buscarTodos(filtro)
                : contactoRepository.buscarPorEmpresa(empresaId, filtro);
    }

    public Optional<Contacto> getContactoById(Long id) {
        return contactoRepository.findById(id);
    }

    public Contacto saveContacto(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    public void deleteContacto(Long id) {
        contactoRepository.findById(id).ifPresent(contactoRepository::delete);
    }

    public List<Contacto> getContactosByEmpresaId(Long empresaId) {
    return contactoRepository.findByEmpresaId(empresaId);
    }
    
}
