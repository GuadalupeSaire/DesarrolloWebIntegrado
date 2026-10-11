package ProyectoFinal.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetallePlanillaReporte {

    private String trabajadorNombre;
    private String documento;

    private BigDecimal sueldoBase;
    private BigDecimal bonificaciones;
    private BigDecimal totalBruto;
    private BigDecimal aportePension;
    private BigDecimal retencionQuinta;
    private BigDecimal totalDescuentos;
    private BigDecimal totalNeto;

    public DetallePlanillaReporte(DetallePlanilla detalle) {

        this.trabajadorNombre =
                detalle.getTrabajador().getNombres()
                + " "
                + detalle.getTrabajador().getApellidos();

        this.documento =
                detalle.getTrabajador().getDocumentoNumero();

        this.sueldoBase =
                detalle.getSueldoBase();

        this.bonificaciones =
                detalle.getBonificaciones();

        this.totalBruto =
                detalle.getTotalBruto();

        this.aportePension =
                detalle.getAportePension();

        this.retencionQuinta =
                detalle.getRetencionQuinta();

        this.totalDescuentos =
                detalle.getDescuentos()
                .add(detalle.getAportePension())
                .add(detalle.getRetencionQuinta());

        this.totalNeto =
                detalle.getTotalNeto();
    }
}