package ProyectoFinal.service;

import ProyectoFinal.model.Empresa;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpresaReportService {

    private final EmpresaService empresaService;

    public EmpresaReportService(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    public byte[] generarPdf() throws JRException {
        List<Empresa> empresas = empresaService.getAllEmpresas();
        ClassPathResource recurso = new ClassPathResource("reports/empresas.jrxml");

        try (InputStream inputStream = recurso.getInputStream()) {
            JasperReport reporte = JasperCompileManager.compileReport(inputStream);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TOTAL_EMPRESAS", empresas.size());
            parametros.put(
                    "FECHA_GENERACION",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            JasperPrint impresion = JasperFillManager.fillReport(
                    reporte,
                    parametros,
                    new JRBeanCollectionDataSource(empresas)
            );

            return JasperExportManager.exportReportToPdf(impresion);
        } catch (Exception e) {
            throw new JRException("Error al generar el PDF del informe de empresas.", e);
        }
    }
}
