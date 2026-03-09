package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Usuario;
import com.sergiocalderon.agendamiento_api.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de usuarios
 * Base URL: /api/usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    // GET /api/usuarios - Listar todos
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioServicio.listarTodos());
    }

    // GET /api/usuarios/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return usuarioServicio.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/usuarios - Registrar nuevo usuario
    @PostMapping
    public ResponseEntity<?> registrar(
            @RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioServicio.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("error", 
                                     e.getMessage()));
        }
    }

    // PUT /api/usuarios/{id} - Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {
        try {
            Usuario actualizado = usuarioServicio
                .actualizar(id, usuario);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/usuarios/{id} - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            usuarioServicio.eliminar(id);
            return ResponseEntity.ok(
                Map.of("mensaje", 
                    "Usuario eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/usuarios/login - Iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credenciales) {
        String email     = credenciales.get("email");
        String contrasena = credenciales.get("contrasena");

        return usuarioServicio.login(email, contrasena)
            .map(u -> ResponseEntity.ok(
                Map.of(
                    "mensaje", "Login exitoso",
                    "usuario", u
                )))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build());
    }
}