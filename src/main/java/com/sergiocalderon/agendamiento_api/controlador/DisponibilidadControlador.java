package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import com.sergiocalderon.agendamiento_api.servicio.DisponibilidadServicio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de disponibilidad
 * Base URL: /api/disponibilidad
 */
@Tag(name = "Disponibilidad", description = "Gestión de horarios disponibles para citas")
@RestController
@RequestMapping("/api/disponibilidad")
@CrossOrigin(origins = "*")
public class DisponibilidadControlador {

    @Autowired
    private DisponibilidadServicio disponibilidadServicio;

    @Operation(summary = "Listar todos los horarios")
    // GET /api/disponibilidad - Listar todos
    @GetMapping
    public ResponseEntity<List<Disponibilidad>> listarTodos() {
        return ResponseEntity.ok(
                disponibilidadServicio.listarTodos());
    }

    @Operation(summary = "Buscar horario por ID")
    // GET /api/disponibilidad/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Integer id) {
        return disponibilidadServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Horarios por fecha", description = "Formato: YYYY-MM-DD")
    // GET /api/disponibilidad/fecha/{fecha} - Por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Disponibilidad>> listarPorFecha(
            @PathVariable String fecha) {
        return ResponseEntity.ok(
                disponibilidadServicio.listarPorFecha(
                        LocalDate.parse(fecha)));
    }

    @Operation(summary = "Horarios disponibles con cupos", description = "Solo retorna horarios activos con cupos libres")
    // GET /api/disponibilidad/disponibles/{fecha} - Con cupos libres
    @GetMapping("/disponibles/{fecha}")
    public ResponseEntity<List<Disponibilidad>> listarDisponibles(
            @PathVariable String fecha) {
        return ResponseEntity.ok(
                disponibilidadServicio.listarDisponiblesPorFecha(
                        LocalDate.parse(fecha)));
    }

    // POST /api/disponibilidad - Crear horario
    @Operation(summary = "Crear horario disponible", description = "disponible=true y cuposOcupados=0 se asignan automáticamente")
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Disponibilidad disponibilidad) {
        try {
            Disponibilidad nueva = disponibilidadServicio
                    .crear(disponibilidad);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            e.getMessage()));
        }
    }

    // PUT /api/disponibilidad/{id} - Actualizar horario
    @Operation(summary = "Actualizar horario")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody Disponibilidad disponibilidad) {
        try {
            return ResponseEntity.ok(
                    disponibilidadServicio.actualizar(
                            id, disponibilidad));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            e.getMessage()));
        }
    }

    // PATCH /api/disponibilidad/{id}/desactivar
    @Operation(summary = "Desactivar horario", description = "Cambia disponible a false sin eliminar")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(
            @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(
                    disponibilidadServicio.desactivar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            e.getMessage()));
        }
    }

    // DELETE /api/disponibilidad/{id} - Eliminar
    @Operation(summary = "Eliminar horario")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id) {
        try {
            disponibilidadServicio.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje",
                    "Horario eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
