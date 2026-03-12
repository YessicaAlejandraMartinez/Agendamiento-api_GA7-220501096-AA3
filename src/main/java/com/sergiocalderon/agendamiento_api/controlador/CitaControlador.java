package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Cita;
import com.sergiocalderon.agendamiento_api.servicio.CitaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de citas
 * Base URL: /api/citas
 */
@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaControlador {

    @Autowired
    private CitaServicio citaServicio;

    // GET /api/citas - Listar todas
    @GetMapping
    public ResponseEntity<List<Cita>> listarTodas() {
        return ResponseEntity.ok(citaServicio.listarTodas());
    }

    // GET /api/citas/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Integer id) {
        return citaServicio.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/citas/cliente/{idCliente} - Citas por cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Cita>> listarPorCliente(
            @PathVariable Integer idCliente) {
        return ResponseEntity.ok(
            citaServicio.listarPorCliente(idCliente));
    }

    // GET /api/citas/estado/{estado} - Citas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Cita>> listarPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
            citaServicio.listarPorEstado(estado));
    }

    // GET /api/citas/fecha/{fecha} - Citas por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Cita>> listarPorFecha(
            @PathVariable String fecha) {
        return ResponseEntity.ok(
            citaServicio.listarPorFecha(LocalDate.parse(fecha)));
    }

    // POST /api/citas - Crear nueva cita
    @PostMapping
    public ResponseEntity<?> crearCita(
            @RequestBody Cita cita) {
        try {
            Cita nueva = citaServicio.crearCita(cita);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("error", 
                                     e.getMessage()));
        }
    }

    // PUT /api/citas/{id} - Modificar cita
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarCita(
            @PathVariable Integer id,
            @RequestBody Cita cita) {
        try {
            return ResponseEntity.ok(
                citaServicio.modificarCita(id, cita));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("error", 
                                     e.getMessage()));
        }
    }

    // PATCH /api/citas/{id}/cancelar - Cancelar cita
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarCita(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            String motivo = body.getOrDefault("motivo", 
                "Sin motivo especificado");
            return ResponseEntity.ok(
                citaServicio.cancelarCita(id, motivo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                                .body(Map.of("error", 
                                    e.getMessage()));
        }
    }

    // DELETE /api/citas/{id} - Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCita(
            @PathVariable Integer id) {
        try {
            citaServicio.eliminarCita(id);
            return ResponseEntity.ok(
                Map.of("mensaje", 
                    "Cita eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH /api/citas/{id}/confirmar - Confirmar cita
@PatchMapping("/{id}/confirmar")
public ResponseEntity<?> confirmarCita(
        @PathVariable Integer id) {
    try {
        return ResponseEntity.ok(
            citaServicio.confirmarCita(id));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                             .body(Map.of("error",
                                 e.getMessage()));
    }
}
}
