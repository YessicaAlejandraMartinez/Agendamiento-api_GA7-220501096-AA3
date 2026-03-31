package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Usuario;
import com.sergiocalderon.agendamiento_api.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST para gestión de usuarios
 * Base URL: /api/usuarios
 */
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Operation(summary = "Listar todos los usuarios", description = "Retorna la lista completa de usuarios registrados")
    // GET /api/usuarios - Listar todos
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioServicio.listarTodos());
    }

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    // GET /api/usuarios/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return usuarioServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario. Valida email duplicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Email ya registrado")
    })
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

    @Operation(summary = "Actualizar usuario", description = "Permite actualizar todos los campos incluyendo rol")
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

    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    // DELETE /api/usuarios/{id} - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            usuarioServicio.eliminar(id);
            return ResponseEntity.ok(
                    Map.of("mensaje",
                            "Usuario eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Login de usuario", description = "Autentica con email y contraseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    // POST /api/usuarios/login - Iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String contrasena = credenciales.get("contrasena");

        return usuarioServicio.login(email, contrasena)
                .map(u -> ResponseEntity.ok(
                        Map.of(
                                "mensaje", "Login exitoso",
                                "usuario", u)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .build());
    }
}