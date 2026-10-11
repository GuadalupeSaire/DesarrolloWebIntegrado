package ProyectoFinal.controller;

import ProyectoFinal.service.EmpresaReportService;
import ProyectoFinal.service.TrabajadorReportService;
import ProyectoFinal.service.UsuarioService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/informes")
public class InformeController {

    private final EmpresaReportService empresaReportService;
    private final TrabajadorReportService trabajadorReportService;
    private final UsuarioService usuarioService;

    public InformeController(
            EmpresaReportService empresaReportService,
            TrabajadorReportService trabajadorReportService,
            UsuarioService usuarioService) {
        this.empresaReportService = empresaReportService;
        this.trabajadorReportService = trabajadorReportService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/empresas/pdf")
    public ResponseEntity<byte[]> generarInformeEmpresas() {
        try {
            byte[] pdf = empresaReportService.generarPdf();
            return respuestaPdf(pdf, "informe-general-empresas.pdf");
        } catch (JRException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/trabajadores/pdf")
    public ResponseEntity<byte[]> generarInformeTrabajadores(
            Authentication authentication) {
        try {
            boolean esAdministrador = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMINISTRADOR"
                            .equals(authority.getAuthority()));

            if (esAdministrador) {
                byte[] pdf = trabajadorReportService.generarPdf();
                return respuestaPdf(pdf, "informe-general-trabajadores.pdf");
            }

            Long empresaId = usuarioService
                    .obtenerEmpresaActivaIdPorCorreo(authentication.getName())
                    .orElse(null);

            if (empresaId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            byte[] pdf = trabajadorReportService.generarPdfPorEmpresa(empresaId);
            return respuestaPdf(pdf, "informe-trabajadores-empresa-" + empresaId + ".pdf");
        } catch (JRException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<byte[]> respuestaPdf(byte[] pdf, String nombreArchivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename(nombreArchivo)
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
