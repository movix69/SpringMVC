package com.spring.mvc.controller;

import com.hibernate.entity.Rol;
import com.hibernate.entity.Usuario;
import com.hibernate.repository.IRolRepository;
import com.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IRolRepository rolRepository;

    /**
     * Mostrar página de login
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("logout", "Has cerrado sesión correctamente");
        }

        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    /**
     * Mostrar página de registro
     */
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    /**
     * Procesar el registro de un nuevo usuario
     */
    @PostMapping("/registro")
    public String registroPost(@Valid @ModelAttribute("usuario") Usuario usuario,
                               BindingResult bindingResult,
                               Model model) {

        // Validar si hay errores de validación
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        try {
            // Crear el usuario
            Usuario nuevoUsuario = usuarioService.registrar(usuario);

            // Asignar rol por defecto (VIEWER)
            Optional<Rol> rolViewer = rolRepository.findByNombre("VIEWER");
            if (rolViewer.isPresent()) {
                nuevoUsuario.addRol(rolViewer.get());
                usuarioService.actualizar(nuevoUsuario);
            }

            model.addAttribute("success", "Registro completado. Por favor inicia sesión.");
            model.addAttribute("usuario", new Usuario());
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    /**
     * Mostrar página de inicio (dashboard)
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("usuario", auth.getName());
            return "redirect:/peliculas";
        }

        return "redirect:/login";
    }

    /**
     * Información del usuario actual
     */
    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuario = usuarioService.obtenerPorUsername(username);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "perfil";
        }

        return "redirect:/login";
    }

    /**
     * Actualizar perfil del usuario
     */
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute("usuario") Usuario usuarioActualizado,
                                   BindingResult bindingResult,
                                   Model model) {

        if (bindingResult.hasErrors()) {
            return "perfil";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<Usuario> usuarioOptional = usuarioService.obtenerPorUsername(username);

            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                usuario.setNombre(usuarioActualizado.getNombre());
                usuario.setEmail(usuarioActualizado.getEmail());

                usuarioService.actualizar(usuario);
                model.addAttribute("success", "Perfil actualizado correctamente");
                model.addAttribute("usuario", usuario);
                return "perfil";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "perfil";
    }
}
