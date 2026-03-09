package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Usuario;
import com.sergiocalderon.agendamiento_api.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

// Servicio que contiene la lógica de negocio para Usuario

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }

    // Buscar usuario por email
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    // Registrar nuevo usuario
    public Usuario registrar(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException(
                "Ya existe un usuario con el email: " + 
                usuario.getEmail());
        }
        return usuarioRepositorio.save(usuario);
    }

    // Actualizar usuario existente
    public Usuario actualizar(Integer id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException(
                "Usuario no encontrado con ID: " + id));

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setTelefono(usuarioActualizado.getTelefono());

        return usuarioRepositorio.save(usuario);
    }

    // Eliminar usuario
    public void eliminar(Integer id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException(
                "Usuario no encontrado con ID: " + id);
        }
        usuarioRepositorio.deleteById(id);
    }

    // Login - validar credenciales
    public Optional<Usuario> login(String email, String contrasena) {
        return usuarioRepositorio.findByEmail(email)
            .filter(u -> u.getContrasena().equals(contrasena));
    }
}
