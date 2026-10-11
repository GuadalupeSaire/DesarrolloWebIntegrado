package ProyectoFinal.service;

import ProyectoFinal.model.DetallePlanilla;
import ProyectoFinal.model.Empresa;
import ProyectoFinal.model.Planilla;
import ProyectoFinal.model.Trabajador;

import ProyectoFinal.repository.DetallePlanillaRepository;
import ProyectoFinal.repository.EmpresaRepository;
import ProyectoFinal.repository.PlanillaRepository;
import ProyectoFinal.repository.TrabajadorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PlanillaService {

    private final PlanillaRepository planillaRepository;
    private final DetallePlanillaRepository detallePlanillaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EmpresaRepository empresaRepository;

    public PlanillaService(
            PlanillaRepository planillaRepository,
            DetallePlanillaRepository detallePlanillaRepository,
            TrabajadorRepository trabajadorRepository,
            EmpresaRepository empresaRepository) {

        this.planillaRepository = planillaRepository;
        this.detallePlanillaRepository = detallePlanillaRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<Planilla> getAllPlanillas() {
        return planillaRepository.findAll();
    }

    public Optional<Planilla> getPlanillaById(Long id) {
        return planillaRepository.findById(id);
    }

    public Optional<Planilla> getPlanillaByIdAndEmpresaId(Long id, Long empresaId) {
        return planillaRepository.findByIdAndEmpresaId(id, empresaId);
    }

    public long contarPlanillas(){
        return planillaRepository.count();
    }

    public Planilla savePlanilla(Planilla planilla) {
        return planillaRepository.save(planilla);
    }

    @Transactional
    public void deletePlanilla(Long id) {
        if (!planillaRepository.existsById(id)) {
            return;
        }
        detallePlanillaRepository.deleteByPlanillaId(id);
        planillaRepository.deleteById(id);
    }

    public List<Planilla> getPlanillasByEmpresaId(Long empresaId) {
        return planillaRepository.findByEmpresaId(empresaId);
    }

    public List<DetallePlanilla> getDetallesByPlanillaId(Long planillaId) {

        return detallePlanillaRepository
                .findByPlanillaIdWithTrabajador(planillaId);
    }

    @Transactional
    public Planilla calcularPlanilla(
            Long empresaId,
            Integer anio,
            Integer mes) {

        // 1. Buscar empresa
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa no encontrada: " + empresaId
                        ));

        // 2. Verificar que no exista otra planilla
        if (planillaRepository
                .findByEmpresaIdAndPeriodoAnioAndPeriodoMes(
                        empresaId,
                        anio,
                        mes)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Ya existe una planilla para esta empresa y período."
            );
        }

        // 3. Crear cabecera de planilla
        Planilla planilla = new Planilla();

        planilla.setEmpresa(empresa);
        planilla.setPeriodoAnio(anio);
        planilla.setPeriodoMes(mes);
        planilla.setEstado(Planilla.EstadoPlanilla.BORRADOR);

        planilla.setTotalBruto(BigDecimal.ZERO);
        planilla.setTotalDescuentos(BigDecimal.ZERO);
        planilla.setTotalNeto(BigDecimal.ZERO);

        // Guardamos primero la cabecera
        planilla = planillaRepository.save(planilla);

        // 4. Buscar trabajadores activos
        List<Trabajador> trabajadores =
                trabajadorRepository
                        .findByEmpresaIdAndActivoTrue(empresaId);

        if (trabajadores.isEmpty()) {
            throw new IllegalArgumentException(
                    "La empresa no tiene trabajadores activos."
            );
        }

        // 5. Variables para acumular totales
        BigDecimal totalBruto = BigDecimal.ZERO;
        BigDecimal totalDescuentos = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;

        // 6. Calcular cada trabajador
        for (Trabajador trabajador : trabajadores) {

            DetallePlanilla detalle = new DetallePlanilla();

            detalle.setPlanilla(planilla);
            detalle.setTrabajador(trabajador);

            // Datos iniciales
            detalle.setDiasLaborados(30);
            detalle.setFaltas(0);
            detalle.setHorasExtra(BigDecimal.ZERO);
            detalle.setSubsidios(BigDecimal.ZERO);

            BigDecimal sueldoBase = trabajador.getSueldoBase();

            detalle.setSueldoBase(sueldoBase);
            detalle.setBonificaciones(BigDecimal.ZERO);
            detalle.setDescuentos(BigDecimal.ZERO);
            BigDecimal tasaPension = trabajador.getTasaPension() == null
                    ? BigDecimal.ZERO : trabajador.getTasaPension();
            detalle.setAportePension(sueldoBase.multiply(tasaPension)
                    .setScale(2, java.math.RoundingMode.HALF_UP));
            detalle.setRetencionQuinta(BigDecimal.ZERO);

            // 7. Calcular sueldo bruto
            BigDecimal bruto = sueldoBase
                    .add(detalle.getBonificaciones())
                    .add(detalle.getSubsidios());

            detalle.setTotalBruto(bruto);

            // 8. Calcular descuentos
            BigDecimal descuentos = detalle.getDescuentos()
                    .add(detalle.getAportePension())
                    .add(detalle.getRetencionQuinta());

            // 9. Calcular sueldo neto
            BigDecimal neto = bruto.subtract(descuentos);

            detalle.setTotalNeto(neto);

            // 10. EsSalud del empleador
            BigDecimal essalud = bruto
                    .multiply(new BigDecimal("0.09"));

            detalle.setEssaludEmpleador(essalud);

            // 11. Guardar detalle
            detallePlanillaRepository.save(detalle);

            // 12. Acumular totales
            totalBruto = totalBruto.add(bruto);
            totalDescuentos = totalDescuentos.add(descuentos);
            totalNeto = totalNeto.add(neto);
        }

        // 13. Actualizar totales de la planilla
        planilla.setTotalBruto(totalBruto);
        planilla.setTotalDescuentos(totalDescuentos);
        planilla.setTotalNeto(totalNeto);

        planilla.setEstado(
                Planilla.EstadoPlanilla.CALCULADA
        );

        // 14. Guardar planilla final
        return planillaRepository.save(planilla);
    }

    public Optional<DetallePlanilla> getDetalleById(Long detalleId) {
        return detallePlanillaRepository.findById(detalleId);
    }

    @Transactional
    public DetallePlanilla actualizarNovedad(Long detalleId, Integer faltas,
            BigDecimal horasExtra, BigDecimal bonificaciones, BigDecimal subsidios,
            BigDecimal otrosDescuentos) {
        DetallePlanilla detalle = detallePlanillaRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado."));
        if (detalle.getPlanilla().getEstado() == Planilla.EstadoPlanilla.CERRADA) {
            throw new IllegalStateException("No se puede modificar una planilla cerrada.");
        }
        if (faltas == null || faltas < 0 || faltas > 30 || horasExtra == null
                || horasExtra.signum() < 0 || bonificaciones == null
                || bonificaciones.signum() < 0 || subsidios == null
                || subsidios.signum() < 0 || otrosDescuentos == null
                || otrosDescuentos.signum() < 0) {
            throw new IllegalArgumentException("Revisa las novedades: no pueden ser negativas y las faltas deben estar entre 0 y 30.");
        }

        detalle.setFaltas(faltas);
        detalle.setDiasLaborados(30 - faltas);
        detalle.setHorasExtra(horasExtra);
        detalle.setBonificaciones(bonificaciones);
        detalle.setSubsidios(subsidios);
        BigDecimal sueldoBase = detalle.getTrabajador().getSueldoBase();
        BigDecimal remuneracionDias = sueldoBase.multiply(BigDecimal.valueOf(30 - faltas))
                .divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal valorHora = sueldoBase.divide(BigDecimal.valueOf(240), 6, java.math.RoundingMode.HALF_UP);
        BigDecimal horasCon25 = horasExtra.min(BigDecimal.valueOf((30 - faltas) * 2L));
        BigDecimal horasCon35 = horasExtra.subtract(horasCon25);
        BigDecimal importeHoras = valorHora.multiply(horasCon25).multiply(new BigDecimal("1.25"))
                .add(valorHora.multiply(horasCon35).multiply(new BigDecimal("1.35")))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        detalle.setImporteHorasExtra(importeHoras);
        BigDecimal bruto = remuneracionDias.add(importeHoras).add(bonificaciones).add(subsidios);
        BigDecimal tasa = detalle.getTrabajador().getTasaPension() == null
                ? BigDecimal.ZERO : detalle.getTrabajador().getTasaPension();
        BigDecimal pension = bruto.multiply(tasa).setScale(2, java.math.RoundingMode.HALF_UP);
        detalle.setSueldoBase(remuneracionDias);
        detalle.setAportePension(pension);
        detalle.setDescuentos(otrosDescuentos);
        detalle.setTotalBruto(bruto);
        detalle.setTotalNeto(bruto.subtract(pension).subtract(detalle.getRetencionQuinta()).subtract(otrosDescuentos));
        detalle.setEssaludEmpleador(bruto.multiply(new BigDecimal("0.09")).setScale(2, java.math.RoundingMode.HALF_UP));
        detalle = detallePlanillaRepository.save(detalle);
        recalcularTotales(detalle.getPlanilla());
        return detalle;
    }

    private void recalcularTotales(Planilla planilla) {
        List<DetallePlanilla> detalles = detallePlanillaRepository.findByPlanillaIdWithTrabajador(planilla.getId());
        planilla.setTotalBruto(detalles.stream().map(DetallePlanilla::getTotalBruto).reduce(BigDecimal.ZERO, BigDecimal::add));
        planilla.setTotalDescuentos(detalles.stream().map(d -> d.getAportePension().add(d.getRetencionQuinta()).add(d.getDescuentos())).reduce(BigDecimal.ZERO, BigDecimal::add));
        planilla.setTotalNeto(detalles.stream().map(DetallePlanilla::getTotalNeto).reduce(BigDecimal.ZERO, BigDecimal::add));
        planilla.setEstado(Planilla.EstadoPlanilla.CALCULADA);
        planillaRepository.save(planilla);
    }
}
