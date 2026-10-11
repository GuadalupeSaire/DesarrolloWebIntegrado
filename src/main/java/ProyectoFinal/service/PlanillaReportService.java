package ProyectoFinal.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ProyectoFinal.model.DetallePlanilla;
import ProyectoFinal.model.DetallePlanillaReporte;
import ProyectoFinal.model.Planilla;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanillaReportService {

    private final PlanillaService planillaService;

    public PlanillaReportService(PlanillaService planillaService) {
        this.planillaService = planillaService;
    }

    public byte[] generarPdf(Long planillaId) throws JRException {
        return generarPdf(planillaId, null);
    }

    public byte[] generarBoletaPdf(Long planillaId, Long detalleId) throws JRException {
        return generarPdf(planillaId, detalleId);
    }

    private byte[] generarPdf(Long planillaId, Long detalleId) throws JRException {

        // =========================================
        // 1. OBTENER LA PLANILLA
        // =========================================

        Planilla planilla = planillaService
                .getPlanillaById(planillaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Planilla no encontrada: " + planillaId
                        )
                );


        // =========================================
        // 2. OBTENER LOS DETALLES
        // =========================================

        List<DetallePlanilla> detalles = planillaService.getDetallesByPlanillaId(planillaId);
        if (detalleId != null) {
            detalles = detalles.stream().filter(d -> d.getId().equals(detalleId)).toList();
            if (detalles.isEmpty()) {
                throw new IllegalArgumentException("El trabajador no pertenece a esta planilla.");
            }
        }


        // =========================================
        // 3. CONVERTIR DATOS PARA JASPER
        // =========================================

        List<DetallePlanillaReporte> datosReporte =
                detalles.stream()
                        .map(DetallePlanillaReporte::new)
                        .toList();


        // =========================================
        // 4. CARGAR ARCHIVO JRXML
        // =========================================

        ClassPathResource resource =
                new ClassPathResource("reports/planilla.jrxml");

        System.out.println("=================================");
        System.out.println("JASPER REPORT");
        System.out.println("Archivo existe: " + resource.exists());
        System.out.println("Archivo: " + resource.getDescription());
        System.out.println("=================================");


        try (InputStream inputStream = resource.getInputStream()) {

            // =========================================
            // 5. COMPILAR JRXML
            // =========================================

            System.out.println("PASO 1: Cargando JRXML...");

            JasperReport reporte =
                    JasperCompileManager.compileReport(inputStream);

            System.out.println(
                    "PASO 2: JRXML COMPILADO CORRECTAMENTE"
            );


            // =========================================
            // 6. CREAR PARÁMETROS
            // =========================================

            Map<String, Object> parametros = new HashMap<>();


            // Empresa
            parametros.put(
                    "EMPRESA",
                    planilla.getEmpresa().getRazonSocial()
            );


            // RUC
            parametros.put(
                    "RUC",
                    planilla.getEmpresa().getRuc()
            );


            // Período
            parametros.put(
                    "PERIODO",
                    planilla.getPeriodoMes()
                            + " / "
                            + planilla.getPeriodoAnio()
            );


            // Estado
            parametros.put(
                    "ESTADO",
                    planilla.getEstado().toString()
            );
            parametros.put("TIPO_DOCUMENTO", detalleId == null ? "REPORTE DE PLANILLA" : "BOLETA DE PAGO");


            // =========================================
            // 7. TOTALES DE LA PLANILLA
            // =========================================

            parametros.put(
                    "TOTAL_BRUTO",
                    detalles.stream().map(DetallePlanilla::getTotalBruto).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );


            parametros.put(
                    "TOTAL_DESCUENTOS",
                    detalles.stream().map(d -> d.getAportePension().add(d.getRetencionQuinta()).add(d.getDescuentos())).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );


            parametros.put(
                    "TOTAL_NETO",
                    detalles.stream().map(DetallePlanilla::getTotalNeto).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );


            // =========================================
            // 8. CREAR DATASOURCE
            // =========================================

            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(datosReporte);


            // =========================================
            // 9. LLENAR REPORTE
            // =========================================

            System.out.println("PASO 3: Llenando reporte...");

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            reporte,
                            parametros,
                            dataSource
                    );

            System.out.println(
                    "PASO 4: Reporte llenado correctamente"
            );


            // =========================================
            // 10. EXPORTAR A PDF
            // =========================================

            byte[] pdf =
                    JasperExportManager.exportReportToPdf(
                            jasperPrint
                    );

            System.out.println("PASO 5: PDF GENERADO");
            System.out.println(
                    "Tamaño: " + pdf.length + " bytes"
            );


            // =========================================
            // 11. DEVOLVER PDF
            // =========================================

            return pdf;


        } catch (Exception e) {

            // =========================================
            // MANEJO DE ERRORES
            // =========================================

            System.err.println("=================================");
            System.err.println("ERROR REAL DE JASPER");
            System.err.println("=================================");

            e.printStackTrace();


            System.err.println("=================================");
            System.err.println("CAUSA");
            System.err.println("=================================");

            Throwable causa = e.getCause();

            while (causa != null) {

                System.err.println(
                        "TIPO: "
                                + causa.getClass().getName()
                );

                System.err.println(
                        "MENSAJE: "
                                + causa.getMessage()
                );

                causa.printStackTrace();

                causa = causa.getCause();
            }


            // =========================================
            // LANZAR ERROR
            // =========================================

            throw new JRException(
                    "Error al generar el PDF de la planilla.",
                    e
            );
        }
    }
}
