package com.spring.mvc.service;

import com.hibernate.entity.Usuario;
import com.hibernate.repository.IUsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(IUsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Carga los detalles del usuario por username para Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        Usuario u = usuario.get();

        if (!u.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        Collection<GrantedAuthority> autoridades = new ArrayList<>();
        u.getRoles().forEach(rol -> {
            autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
        });

        return new User(u.getUsername(), u.getPassword(), autoridades);
    }

    /**
     * Obtiene un usuario por username
     */
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Obtiene un usuario por email
     */
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Registra un nuevo usuario
     */
    public Usuario registrar(Usuario usuario) throws Exception {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new Exception("El username ya existe: " + usuario.getUsername());
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("El email ya existe: " + usuario.getEmail());
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene todos los usuarios
     */
    public Iterable<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Actualiza un usuario
     */
    public Usuario actualizar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario
     */
    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }
}
