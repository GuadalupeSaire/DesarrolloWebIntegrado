package ProyectoFinal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ProyectoFinal.model.Rol;
import ProyectoFinal.repository.RolRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;

    public DataInitializer(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {

        crearRol("Administrador", "Acceso administrativo al sistema");
        crearRol("Reclutador", "Gestión de procesos de selección");
        crearRol("Planillas", "Gestión de trabajadores y planillas");
    }

    private void crearRol(String nombre, String descripcion) {

        if (rolRepository.findByNombre(nombre).isEmpty()) {

            Rol rol = new Rol();

            rol.setNombre(nombre);
            rol.setDescripcion(descripcion);

            rolRepository.save(rol);
        }
    }
}