package ProyectoFinal.controller;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.model.Planilla;
import ProyectoFinal.service.EmpresaService;
import ProyectoFinal.service.PlanillaService;
import ProyectoFinal.service.TrabajadorService;
import ProyectoFinal.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class InicioController {

    private static final String[] COLORES_ESTADO = {
            "#64748B", "#173B67", "#16806A", "#C17A21"
    };

    private final EmpresaService empresaService;
    private final TrabajadorService trabajadorService;
    private final PlanillaService planillaService;
    private final UsuarioService usuarioService;

    public InicioController(
            EmpresaService empresaService,
            TrabajadorService trabajadorService,
            PlanillaService planillaService,
            UsuarioService usuarioService) {
        this.empresaService = empresaService;
        this.trabajadorService = trabajadorService;
        this.planillaService = planillaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model, Authentication authentication) {
        boolean esAdmin = tieneRol(authentication, "ROLE_ADMINISTRADOR");
        boolean esPlanillas = tieneRol(authentication, "ROLE_PLANILLAS");
        List<Planilla> planillas;

        model.addAttribute("esAdministrador", esAdmin);
        model.addAttribute("esPlanillas", esPlanillas);

        if (esAdmin) {
            planillas = planillaService.getAllPlanillas();
            model.addAttribute("totalEmpresas", empresaService.getAllEmpresas().size());
            model.addAttribute("totalTrabajadores", trabajadorService.contarTrabajadores());
        } else {
            Long empresaId = usuarioService
                    .obtenerEmpresaActivaIdPorCorreo(authentication.getName())
                    .orElse(null);

            if (empresaId == null) {
                planillas = List.of();
                model.addAttribute("totalEmpresas", 0);
                model.addAttribute("totalTrabajadores", 0);
            } else {
                Empresa empresa = empresaService.buscarPorId(empresaId).orElse(null);
                model.addAttribute("empresaActual", empresa);
                model.addAttribute("totalEmpresas", 1);
                model.addAttribute(
                        "totalTrabajadores",
                        trabajadorService.getTrabajadoresByEmpresaId(empresaId).size());
                planillas = planillaService.getPlanillasByEmpresaId(empresaId);
            }
        }

        model.addAttribute("totalPlanillas", planillas.size());

        List<Map<String, Object>> datosMensuales = crearDatosMensuales(planillas);
        model.addAttribute("netoMensual", datosMensuales);
        model.addAttribute("estadosPlanilla", crearDatosEstados(planillas));
        model.addAttribute("estadoGradient", crearGradienteEstados(planillas));

        return "inicio/inicio";
    }

    private List<Map<String, Object>> crearDatosMensuales(List<Planilla> planillas) {
        YearMonth mesActual = YearMonth.now();
        List<Map<String, Object>> datos = new ArrayList<>();
        BigDecimal maximo = BigDecimal.ZERO;

        for (int desplazamiento = 5; desplazamiento >= 0; desplazamiento--) {
            YearMonth mes = mesActual.minusMonths(desplazamiento);
            BigDecimal neto = planillas.stream()
                    .filter(planilla -> planilla.getPeriodoAnio().equals(mes.getYear())
                            && planilla.getPeriodoMes().equals(mes.getMonthValue()))
                    .map(planilla -> planilla.getTotalNeto() == null
                            ? BigDecimal.ZERO : planilla.getTotalNeto())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> dato = new HashMap<>();
            String etiqueta = mes.getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es"))
                    .replace(".", "");
            etiqueta = etiqueta.substring(0, 1).toUpperCase(Locale.forLanguageTag("es"))
                    + etiqueta.substring(1);

            dato.put("etiqueta", etiqueta);
            dato.put("neto", neto);
            datos.add(dato);
            if (neto.compareTo(maximo) > 0) {
                maximo = neto;
            }
        }

        for (Map<String, Object> dato : datos) {
            BigDecimal neto = (BigDecimal) dato.get("neto");
            int altura = maximo.signum() == 0
                    ? 0
                    : neto.multiply(BigDecimal.valueOf(100))
                            .divide(maximo, 0, java.math.RoundingMode.HALF_UP)
                            .intValue();
            dato.put("altura", altura);
        }

        return datos;
    }

    private List<Map<String, Object>> crearDatosEstados(List<Planilla> planillas) {
        List<Map<String, Object>> datos = new ArrayList<>();
        Planilla.EstadoPlanilla[] estados = Planilla.EstadoPlanilla.values();

        for (int i = 0; i < estados.length; i++) {
            Planilla.EstadoPlanilla estado = estados[i];
            long cantidad = planillas.stream()
                    .filter(planilla -> planilla.getEstado() == estado)
                    .count();

            Map<String, Object> dato = new HashMap<>();
            dato.put("etiqueta", nombreEstado(estado));
            dato.put("cantidad", cantidad);
            dato.put("color", COLORES_ESTADO[i % COLORES_ESTADO.length]);
            dato.put("porcentaje", planillas.isEmpty()
                    ? 0
                    : Math.round(cantidad * 100.0 / planillas.size()));
            datos.add(dato);
        }

        return datos;
    }

    private String crearGradienteEstados(List<Planilla> planillas) {
        if (planillas.isEmpty()) {
            return "conic-gradient(#E2E8F0 0% 100%)";
        }

        StringBuilder gradiente = new StringBuilder("conic-gradient(");
        long acumulado = 0;
        Planilla.EstadoPlanilla[] estados = Planilla.EstadoPlanilla.values();

        for (int i = 0; i < estados.length; i++) {
            Planilla.EstadoPlanilla estado = estados[i];
            long cantidad = planillas.stream()
                    .filter(planilla -> planilla.getEstado() == estado)
                    .count();
            long inicio = Math.round(acumulado * 100.0 / planillas.size());
            acumulado += cantidad;
            long fin = i == estados.length - 1
                    ? 100
                    : Math.round(acumulado * 100.0 / planillas.size());

            if (cantidad > 0) {
                if (gradiente.charAt(gradiente.length() - 1) != '(') {
                    gradiente.append(", ");
                }
                gradiente.append(COLORES_ESTADO[i % COLORES_ESTADO.length])
                        .append(' ').append(inicio).append("% ")
                        .append(fin).append('%');
            }
        }

        return gradiente.append(')').toString();
    }

    private String nombreEstado(Planilla.EstadoPlanilla estado) {
        return switch (estado) {
            case BORRADOR -> "Borrador";
            case CALCULADA -> "Calculada";
            case CERRADA -> "Cerrada";
        };
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> rol.equals(authority.getAuthority()));
    }
}
