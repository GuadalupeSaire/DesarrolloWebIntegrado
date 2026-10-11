package ProyectoFinal.config;

import ProyectoFinal.service.UsuarioDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            UsuarioDetailsService usuarioDetailsService,
            PasswordEncoder passwordEncoder) {

        this.usuarioDetailsService = usuarioDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        .requestMatchers(
                                "/usuarios/**",
                                "/empresas/**",
                                "/informes/empresas/pdf")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/contactos/nuevo", "/contactos/editar/**")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/contactos/guardar", "/contactos/eliminar/**")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers("/contactos/**")
                        .authenticated()

                        .requestMatchers(
                                "/trabajadores/**",
                                "/informes/trabajadores/pdf")
                        .hasAnyRole("ADMINISTRADOR", "PLANILLAS")

                        .requestMatchers("/planillas/**")
                        .hasAnyRole("ADMINISTRADOR", "PLANILLAS")

                        .requestMatchers(
                                "/vacantes/**",
                                "/candidatos/**",
                                "/postulaciones/**")
                        .hasAnyRole("ADMINISTRADOR", "JEFE_RECLUTAMIENTO", "ANALISTA", "ASISTENTE")

                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/inicio", true)
                        .permitAll());

        return http.build();
    }
}
