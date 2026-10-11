package ProyectoFinal.controller;

import ProyectoFinal.model.Contacto;
import ProyectoFinal.model.Empresa;
import ProyectoFinal.service.ContactoService;
import ProyectoFinal.service.EmpresaService;
import ProyectoFinal.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/contactos")
public class ContactoController {

    private final ContactoService contactoService;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;

    public ContactoController(ContactoService contactoService,
            EmpresaService empresaService, UsuarioService usuarioService) {
        this.contactoService = contactoService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String q,
            Model model, Authentication authentication) {
        boolean administrador = esAdministrador(authentication);
        Long empresaId = administrador ? null : obtenerEmpresaAsignadaId(authentication);
        model.addAttribute("contactos", contactoService.buscar(empresaId, q));
        model.addAttribute("esAdministrador", administrador);
        model.addAttribute("busqueda", q);
        if (!administrador) {
            model.addAttribute("empresaActual", empresaService.buscarPorId(empresaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada.")));
        }
        return "contactos/contactos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        prepararFormulario(model, new Contacto());
        return "contactos/contacto-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Contacto contacto = contactoService.getContactoById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contacto no encontrado."));
        prepararFormulario(model, contacto);
        return "contactos/contacto-form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("contacto") Contacto contacto,
            BindingResult resultado, @RequestParam Long empresaId, Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("empresas", empresaService.getAllEmpresas());
            model.addAttribute("empresaSeleccionadaId", empresaId);
            return "contactos/contacto-form";
        }
        Empresa empresa = empresaService.buscarPorId(empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada."));
        contacto.setEmpresa(empresa);
        contactoService.saveContacto(contacto);
        return "redirect:/contactos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        contactoService.deleteContacto(id);
        return "redirect:/contactos";
    }

    private void prepararFormulario(Model model, Contacto contacto) {
        model.addAttribute("contacto", contacto);
        model.addAttribute("empresas", empresaService.getAllEmpresas());
        model.addAttribute("empresaSeleccionadaId",
                contacto.getEmpresa() == null ? null : contacto.getEmpresa().getId());
    }

    private Long obtenerEmpresaAsignadaId(Authentication authentication) {
        return usuarioService.obtenerEmpresaActivaIdPorCorreo(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "El usuario no tiene una empresa asignada."));
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMINISTRADOR".equals(authority.getAuthority()));
    }
}
