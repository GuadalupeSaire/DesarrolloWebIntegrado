
package ProyectoFinal.controller;

import ProyectoFinal.model.Empresa;
import ProyectoFinal.service.EmpresaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Mostrar tabla
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empresas", empresaService.getAllEmpresas());
        return "empresas/empresas";
    }

    // Mostrar formulario para nueva empresa
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresas/empresa-form";
    }

    // Guardar nueva empresa o actualizar existente
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Empresa empresa) {
        empresaService.guardarEmpresa(empresa);
        return "redirect:/empresas";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Empresa empresa = empresaService.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Empresa no encontrada: " + id));

        model.addAttribute("empresa", empresa);
        return "empresas/empresa-form";
    }

    // Eliminar empresa
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return "redirect:/empresas";
    }
}