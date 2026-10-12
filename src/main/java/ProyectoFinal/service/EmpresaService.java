
package ProyectoFinal.service;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.repository.AuditoriaRepository;
import ProyectoFinal.repository.ContactoRepository;
import ProyectoFinal.repository.EmpresaRepository;
import ProyectoFinal.repository.EtapaPostulacionRepository;
import ProyectoFinal.repository.PostulacionRepository;
import ProyectoFinal.repository.TrabajadorRepository;
import ProyectoFinal.repository.PlanillaRepository;
import ProyectoFinal.repository.DetallePlanillaRepository;
import ProyectoFinal.repository.UsuarioEmpresaRepository;
import ProyectoFinal.repository.VacanteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final PlanillaRepository planillaRepository;
    private final DetallePlanillaRepository detallePlanillaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final ContactoRepository contactoRepository;
    private final EtapaPostulacionRepository etapaPostulacionRepository;
    private final PostulacionRepository postulacionRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final VacanteRepository vacanteRepository;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            TrabajadorRepository trabajadorRepository, 
            PlanillaRepository planillaRepository,
            DetallePlanillaRepository detallePlanillaRepository,
            AuditoriaRepository auditoriaRepository,
            ContactoRepository contactoRepository,
            EtapaPostulacionRepository etapaPostulacionRepository,
            PostulacionRepository postulacionRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            VacanteRepository vacanteRepository) {
        this.empresaRepository = empresaRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.planillaRepository = planillaRepository;
        this.detallePlanillaRepository = detallePlanillaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.contactoRepository = contactoRepository;
        this.etapaPostulacionRepository = etapaPostulacionRepository;
        this.postulacionRepository = postulacionRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.vacanteRepository = vacanteRepository;
    }

    @Transactional
    public void eliminarEmpresa(Long id) {
        if (!empresaRepository.existsById(id)) {
            return;
        }

        // Retirar primero los registros que apuntan a documentos y operaciones hijas.
        etapaPostulacionRepository.deleteByPostulacionVacanteEmpresaId(id);
        postulacionRepository.deleteByVacanteEmpresaId(id);
        detallePlanillaRepository.deleteByPlanillaEmpresaId(id);
        detallePlanillaRepository.deleteByTrabajadorEmpresaId(id);

        // Eliminar los registros propios de la empresa y sus agregados.
        contactoRepository.deleteByEmpresaId(id);
        usuarioEmpresaRepository.deleteByEmpresaId(id);
        // Mantener la trazabilidad histórica aunque ya no exista la empresa.
        auditoriaRepository.desvincularEmpresa(id);
        vacanteRepository.deleteByEmpresaId(id);
        planillaRepository.deleteByEmpresaId(id);
        trabajadorRepository.deleteByEmpresaId(id);
        empresaRepository.deleteById(id);
    }

    // Listar todas las empresas
    public List<Empresa> listarEmpresas() {
        return empresaRepository.findAll();
    }

    // Buscar empresa por ID
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    // Buscar empresa por RUC
    public Optional<Empresa> buscarPorRuc(String ruc) {
        return empresaRepository.findByRuc(ruc);
    }

    public long contarEmpresas(){
        return  empresaRepository.count();
    }

    // Registrar empresa

    
    public Empresa guardarEmpresa(Empresa empresa) {

        Optional<Empresa> existente =
                empresaRepository.findByRuc(empresa.getRuc());

        if (existente.isPresent()
                && !existente.get().getId().equals(empresa.getId())) {

            throw new IllegalArgumentException(
                    "Ya existe otra empresa con ese RUC"
            );
        }

        return empresaRepository.save(empresa);
    }

    // Obtener todas las empresas
    public List<Empresa> getAllEmpresas() {
        return empresaRepository.findAll();
    }


}
