package com.spring.mvc.config;

import com.hibernate.repository.IUsuarioRepository;
import com.spring.mvc.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Encoder para las contraseñas
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Servicio de usuario
     */
    @Bean
    public UsuarioService usuarioService(IUsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        return new UsuarioService(usuarioRepository, passwordEncoder);
    }

    /**
     * Configuración del proveedor de autenticación DAO
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioService usuarioService, BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Configuración del AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    /**
     * Configuración de la cadena de filtros de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Rutas públicas
                .requestMatchers("/", "/index", "/login", "/registro", "/css/**", "/js/**", "/images/**", "/estilos.css", "/style.css").permitAll()
                
                // Rutas protegidas del controlador de películas
                .requestMatchers("/peliculas/nuevo", "/peliculas/guardar", "/peliculas/editar/**", "/peliculas/actualizar", "/peliculas/eliminar/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/peliculas/**").hasAnyRole("ADMIN", "EDITOR", "VIEWER")
                
                // Resto de rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/peliculas", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .authenticationProvider(authenticationProvider)
            .csrf(csrf -> csrf.disable()); // En producción, NO desactivar CSRF

        return http.build();
    }
}
