package ProyectoFinal.service;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.model.Trabajador;
import ProyectoFinal.model.TrabajadorEmpresaReporte;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrabajadorReportService {

    private final EmpresaService empresaService;
    private final TrabajadorService trabajadorService;

    public TrabajadorReportService(
            EmpresaService empresaService,
            TrabajadorService trabajadorService) {
        this.empresaService = empresaService;
        this.trabajadorService = trabajadorService;
    }

    public byte[] generarPdf() throws JRException {
        return generarPdfParaEmpresas(empresaService.getAllEmpresas());
    }

    public byte[] generarPdfPorEmpresa(Long empresaId) throws JRException {
        Empresa empresa = empresaService.buscarPorId(empresaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Empresa no encontrada: " + empresaId));

        return generarPdfParaEmpresas(List.of(empresa));
    }

    private byte[] generarPdfParaEmpresas(List<Empresa> empresas)
            throws JRException {
        List<TrabajadorEmpresaReporte> datosReporte = new ArrayList<>();
        int totalTrabajadores = 0;

        for (Empresa empresa : empresas) {
            List<Trabajador> trabajadores =
                    trabajadorService.getTrabajadoresByEmpresaId(empresa.getId());

            if (trabajadores.isEmpty()) {
                datosReporte.add(new TrabajadorEmpresaReporte(empresa, null));
            } else {
                totalTrabajadores += trabajadores.size();

                for (Trabajador trabajador : trabajadores) {
                    datosReporte.add(
                            new TrabajadorEmpresaReporte(empresa, trabajador)
                    );
                }
            }
        }

        ClassPathResource recurso =
                new ClassPathResource("reports/trabajadores.jrxml");

        try (InputStream inputStream = recurso.getInputStream()) {
            JasperReport reporte =
                    JasperCompileManager.compileReport(inputStream);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TOTAL_EMPRESAS", empresas.size());
            parametros.put("TOTAL_TRABAJADORES", totalTrabajadores);
            parametros.put(
                    "FECHA_GENERACION",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    )
            );

            JasperPrint impresion = JasperFillManager.fillReport(
                    reporte,
                    parametros,
                    new JRBeanCollectionDataSource(datosReporte)
            );

            return JasperExportManager.exportReportToPdf(impresion);
        } catch (Exception e) {
            throw new JRException(
                    "Error al generar el PDF del informe de trabajadores.",
                    e
            );
        }
    }
}