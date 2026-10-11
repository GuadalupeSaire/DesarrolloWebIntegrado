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

        crearRol("Administrador", "Gestión de usuarios, roles y empresas cliente");
        crearRol("Jefe_Reclutamiento", "Define perfiles, aprueba ternas y obtiene el reporte de terna");
        crearRol("Analista", "Gestiona vacantes, evalúa candidatos y actualiza estados");
        crearRol("Asistente", "Registra candidatos, carga CVs y consulta el banco de postulantes");
        crearRol("Planillas", "Gestión de trabajadores, planillas y boletas");
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