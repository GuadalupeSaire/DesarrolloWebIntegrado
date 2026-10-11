package ProyectoFinal.controller;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.model.Trabajador;
import ProyectoFinal.service.EmpresaService;
import ProyectoFinal.service.TrabajadorService;
import ProyectoFinal.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

        private final TrabajadorService trabajadorService;
        private final EmpresaService empresaService;
        private final UsuarioService usuarioService;

        public TrabajadorController(
                        TrabajadorService trabajadorService,
                        EmpresaService empresaService, UsuarioService usuarioService) {

                this.trabajadorService = trabajadorService;
                this.empresaService = empresaService;
                this.usuarioService = usuarioService;
        }

        @GetMapping
        public String listar(Model model, Authentication authentication) {
                boolean esAdmin = esAdministrador(authentication);
                model.addAttribute("esAdministrador", esAdmin);

                if (esAdmin) {
                        model.addAttribute("trabajadores", trabajadorService.getAllTrabajadores());
                        model.addAttribute("empresas", empresaService.getAllEmpresas());
                } else {
                        Empresa empresa = obtenerEmpresaAsignada(authentication);
                        model.addAttribute(
                                        "trabajadores",
                                        trabajadorService.getTrabajadoresByEmpresaId(empresa.getId()));
                        model.addAttribute("empresaActual", empresa);
                }

                return "trabajadores/trabajadores";
        }

        @GetMapping("/nuevo")
        public String nuevo(Model model, Authentication authentication) {
                boolean esAdmin = esAdministrador(authentication);
                model.addAttribute("esAdministrador", esAdmin);
                model.addAttribute("trabajador", new Trabajador());

                if (esAdmin) {
                        model.addAttribute("empresas", empresaService.getAllEmpresas());
                } else {
                        model.addAttribute("empresaActual",
                                        obtenerEmpresaAsignada(authentication));
                }

                return "trabajadores/trabajador-form";
        }

        @PostMapping("/guardar")
        public String guardar(
                        @ModelAttribute Trabajador trabajador,
                        @RequestParam BigDecimal tasaPensionPorcentaje,
                        @RequestParam(required = false) Long empresaId,
                        Authentication authentication) {

                Empresa empresa;

                if (esAdministrador(authentication)) {
                        if (empresaId == null) {
                                throw new IllegalArgumentException(
                                                "Selecciona una empresa.");
                        }

                        empresa = empresaService.buscarPorId(empresaId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Empresa no encontrada: " + empresaId));

                        // Confirma que el ID enviado exista antes de actualizar.
                        if (trabajador.getId() != null) {
                                trabajadorService.getTrabajadorById(trabajador.getId())
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                "Trabajador no encontrado."));
                        }
                } else {
                        empresa = obtenerEmpresaAsignada(authentication);

                        if (trabajador.getId() != null) {
                                trabajadorService.getTrabajadorByIdAndEmpresaId(
                                                trabajador.getId(), empresa.getId())
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                "Trabajador no encontrado en la empresa asignada."));
                        }
                }

                trabajador.setEmpresa(empresa);
                trabajador.setTasaPension(tasaPensionPorcentaje
                                .divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP));
                if ("ONP".equals(trabajador.getRegimenPensionario())
                                && trabajador.getTasaPension().compareTo(new BigDecimal("0.1300")) != 0) {
                        throw new IllegalArgumentException("La tasa ONP debe ser 13%.");
                }
                if ("NINGUNO".equals(trabajador.getRegimenPensionario())) {
                        trabajador.setTasaPension(BigDecimal.ZERO);
                }
                trabajadorService.saveTrabajador(trabajador);

                return "redirect:/trabajadores";
        }

        @GetMapping("/editar/{id}")
        public String editar(
                        @PathVariable Long id,
                        Model model,
                        Authentication authentication) {

                boolean esAdmin = esAdministrador(authentication);
                model.addAttribute("esAdministrador", esAdmin);

                Trabajador trabajador;

                if (esAdmin) {
                        trabajador = trabajadorService.getTrabajadorById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Trabajador no encontrado: " + id));
                        model.addAttribute("empresas", empresaService.getAllEmpresas());
                } else {
                        Empresa empresa = obtenerEmpresaAsignada(authentication);
                        trabajador = trabajadorService.getTrabajadorByIdAndEmpresaId(
                                        id, empresa.getId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Trabajador no encontrado en la empresa asignada."));
                        model.addAttribute("empresaActual", empresa);
                }

                model.addAttribute("trabajador", trabajador);

                return "trabajadores/trabajador-form";
        }

        @PostMapping("/eliminar/{id}")
        public String eliminar(
                        @PathVariable Long id,
                        Authentication authentication) {

                Trabajador trabajador;

                if (esAdministrador(authentication)) {
                        trabajador = trabajadorService.getTrabajadorById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Trabajador no encontrado."));
                } else {
                        Empresa empresa = obtenerEmpresaAsignada(authentication);
                        trabajador = trabajadorService.getTrabajadorByIdAndEmpresaId(
                                        id, empresa.getId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Trabajador no encontrado en la empresa asignada."));
                }

                trabajadorService.deleteTrabajador(trabajador.getId());

                return "redirect:/trabajadores";
        }

        private Empresa obtenerEmpresaAsignada(Authentication authentication) {
                Long empresaId = usuarioService
                                .obtenerEmpresaActivaIdPorCorreo(authentication.getName())
                                .orElseThrow(() -> new IllegalStateException(
                                                "El usuario no tiene una empresa activa asignada."));
                return empresaService.buscarPorId(empresaId)
                                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + empresaId));

        }

        private boolean esAdministrador(Authentication authentication) {
                return authentication.getAuthorities().stream()
                                .anyMatch(autoridad -> "ROLE_ADMINISTRADOR".equals(autoridad.getAuthority()));
        }

}
