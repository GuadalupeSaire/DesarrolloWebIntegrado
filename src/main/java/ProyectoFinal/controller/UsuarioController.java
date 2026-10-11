
package ProyectoFinal.controller;

import ProyectoFinal.model.Usuario;
import ProyectoFinal.service.UsuarioService;
import ProyectoFinal.service.EmpresaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;

    public UsuarioController(
        UsuarioService usuarioService,
        EmpresaService empresaService) {
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
    }

    // Mostrar tabla
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.getAllUsuarios());
        return "usuarios/usuarios";
    }

    // Mostrar formulario para nuevo usuario
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", usuarioService.getAllRoles());
        model.addAttribute("empresas", empresaService.getAllEmpresas());
        model.addAttribute("empresaSeleccionadaId", null);
        model.addAttribute("rolSeleccionadoId", null);

        return "usuarios/usuario-form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Long rolId,
            @RequestParam Long empresaId){

        usuarioService.guardarUsuario(usuario, password, rolId, empresaId);

        return "redirect:/usuarios";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado: " + id));

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", usuarioService.getAllRoles());
        model.addAttribute("empresas", empresaService.getAllEmpresas());
        model.addAttribute("empresaSeleccionadaId", usuarioService.obtenerEmpresasAsignadasId(id).orElse(null));
        model.addAttribute("rolSeleccionadoId", usuarioService.obtenerRolAsignadoId(id).orElse(null));

        return "usuarios/usuario-form";
    }

    // Eliminar usuario
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return "redirect:/usuarios";
    }
}