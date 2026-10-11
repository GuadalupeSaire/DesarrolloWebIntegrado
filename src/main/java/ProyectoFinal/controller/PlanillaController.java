package ProyectoFinal.controller;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.model.Planilla;
import ProyectoFinal.service.EmpresaService;
import ProyectoFinal.service.PlanillaReportService;
import ProyectoFinal.service.PlanillaService;
import ProyectoFinal.service.UsuarioService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/planillas")
public class PlanillaController {

    private final PlanillaService planillaService;
    private final EmpresaService empresaService;
    private final PlanillaReportService planillaReportService;
    private final UsuarioService usuarioService;

    public PlanillaController(
            PlanillaService planillaService,
            EmpresaService empresaService,
            PlanillaReportService planillaReportService,
            UsuarioService usuarioService) {
        this.planillaService = planillaService;
        this.empresaService = empresaService;
        this.planillaReportService = planillaReportService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        boolean esAdmin = esAdministrador(authentication);
        model.addAttribute("esAdministrador", esAdmin);

        if (esAdmin) {
            model.addAttribute("planillas", planillaService.getAllPlanillas());
        } else {
            Empresa empresa = obtenerEmpresaAsignada(authentication);
            model.addAttribute("empresaActual", empresa);
            model.addAttribute(
                    "planillas",
                    planillaService.getPlanillasByEmpresaId(empresa.getId()));
        }

        return "planillas/planillas";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, Authentication authentication) {
        boolean esAdmin = esAdministrador(authentication);
        model.addAttribute("esAdministrador", esAdmin);

        if (esAdmin) {
            model.addAttribute("empresas", empresaService.getAllEmpresas());
        } else {
            model.addAttribute("empresaActual", obtenerEmpresaAsignada(authentication));
        }

        return "planillas/planilla-form";
    }

    @PostMapping("/calcular")
    public String calcular(
            @RequestParam(required = false) Long empresaId,
            @RequestParam Integer anio,
            @RequestParam Integer mes,
            Authentication authentication) {

        Empresa empresa;
        if (esAdministrador(authentication)) {
            if (empresaId == null) {
                throw new IllegalArgumentException("Selecciona una empresa.");
            }
            empresa = empresaService.buscarPorId(empresaId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Empresa no encontrada: " + empresaId));
        } else {
            empresa = obtenerEmpresaAsignada(authentication);
        }

        planillaService.calcularPlanilla(empresa.getId(), anio, mes);
        return "redirect:/planillas";
    }

    @PostMapping("/detalle/{detalleId}/novedades")
    public String actualizarNovedades(
            @PathVariable Long detalleId,
            @RequestParam Integer faltas,
            @RequestParam java.math.BigDecimal horasExtra,
            @RequestParam java.math.BigDecimal bonificaciones,
            @RequestParam java.math.BigDecimal subsidios,
            @RequestParam java.math.BigDecimal otrosDescuentos,
            Authentication authentication) {
        ProyectoFinal.model.DetallePlanilla detalle = planillaService.getDetalleById(detalleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado."));
        Planilla planilla = obtenerPlanillaAccesible(detalle.getPlanilla().getId(), authentication);
        planillaService.actualizarNovedad(detalleId, faltas, horasExtra, bonificaciones,
                subsidios, otrosDescuentos);
        return "redirect:/planillas/ver/" + planilla.getId();
    }

    @GetMapping("/ver/{id}")
    public String ver(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Planilla planilla = obtenerPlanillaAccesible(id, authentication);
        model.addAttribute("planilla", planilla);
        model.addAttribute("detalles", planillaService.getDetallesByPlanillaId(id));
        return "planillas/planilla-detalle";
    }

    @GetMapping("/ver/{id}/pdf")
    public ResponseEntity<byte[]> generarPdf(
            @PathVariable Long id,
            Authentication authentication) {
        Planilla planilla = obtenerPlanillaAccesible(id, authentication);
        try {
            byte[] pdf = planillaReportService.generarPdf(planilla.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.inline()
                            .filename("planilla-" + id + ".pdf")
                            .build());

            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ver/{id}/boleta/{detalleId}/pdf")
    public ResponseEntity<byte[]> generarBoleta(
            @PathVariable Long id,
            @PathVariable Long detalleId,
            Authentication authentication) {
        obtenerPlanillaAccesible(id, authentication);
        try {
            byte[] pdf = planillaReportService.generarBoletaPdf(id, detalleId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("boleta-planilla-" + id + "-detalle-" + detalleId + ".pdf").build());
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Planilla obtenerPlanillaAccesible(Long id, Authentication authentication) {
        if (esAdministrador(authentication)) {
            return planillaService.getPlanillaById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Planilla no encontrada."));
        }

        Empresa empresa = obtenerEmpresaAsignada(authentication);
        return planillaService.getPlanillaByIdAndEmpresaId(id, empresa.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Planilla no encontrada."));
    }

    private Empresa obtenerEmpresaAsignada(Authentication authentication) {
        Long empresaId = usuarioService
                .obtenerEmpresaActivaIdPorCorreo(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El usuario no tiene una empresa activa asignada."));

        return empresaService.buscarPorId(empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Empresa no encontrada."));
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMINISTRADOR"
                        .equals(authority.getAuthority()));
    }
}
