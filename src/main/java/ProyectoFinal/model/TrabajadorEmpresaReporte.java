package ProyectoFinal.model;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class TrabajadorEmpresaReporte {

    private final Long empresaId;
    private final String razonSocial;
    private final String ruc;
    private final String direccion;
    private final String telefono;
    private final String correo;
    private final String estadoEmpresa;

    private final String nombreTrabajador;
    private final String documento;
    private final String cargo;
    private final java.math.BigDecimal sueldoBase;
    private final String fechaIngreso;
    private final String estadoTrabajador;

    public TrabajadorEmpresaReporte(Empresa empresa, Trabajador trabajador) {
        this.empresaId = empresa.getId();
        this.razonSocial = empresa.getRazonSocial();
        this.ruc = empresa.getRuc();
        this.direccion = empresa.getDireccion();
        this.telefono = empresa.getTelefono();
        this.correo = empresa.getCorreo();
        this.estadoEmpresa = Boolean.TRUE.equals(empresa.getEstado())
                ? "Activa"
                : "Inactiva";

        if (trabajador == null) {
            this.nombreTrabajador = "Sin trabajadores registrados";
            this.documento = "";
            this.cargo = "";
            this.sueldoBase = null;
            this.fechaIngreso = "";
            this.estadoTrabajador = "";
        } else {
            this.nombreTrabajador =
                    trabajador.getNombres() + " " + trabajador.getApellidos();
            this.documento = trabajador.getDocumentoNumero();
            this.cargo = trabajador.getCargo();
            this.sueldoBase = trabajador.getSueldoBase();
            this.fechaIngreso = trabajador.getFechaIngreso() == null
                    ? "No registrada"
                    : trabajador.getFechaIngreso()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            this.estadoTrabajador = Boolean.TRUE.equals(trabajador.getActivo())
                    ? "Activo"
                    : "Inactivo";
        }
    }
}