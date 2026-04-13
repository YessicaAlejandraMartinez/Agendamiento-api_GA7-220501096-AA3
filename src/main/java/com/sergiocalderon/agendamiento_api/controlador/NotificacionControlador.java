package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Notificacion;
import com.sergiocalderon.agendamiento_api.servicio.NotificacionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para notificaciones del sistema
 * Base URL: /api/notificaciones
 */
@Tag(name = "Notificaciones",
     description = "Gestión de notificaciones del sistema")
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionControlador {

    @Autowired
    private NotificacionServicio notificacionServicio;

    /* GET /api/notificaciones/usuario/{id} */
    @Operation(summary = "Listar notificaciones de un usuario")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Notificacion>>
            listarPorUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(
            notificacionServicio.listarPorUsuario(id));
    }

    /* GET /api/notificaciones/usuario/{id}/no-leidas */
    @Operation(summary = "Listar notificaciones no leídas")
    @GetMapping("/usuario/{id}/no-leidas")
    public ResponseEntity<List<Notificacion>>
            listarNoLeidas(@PathVariable Integer id) {
        return ResponseEntity.ok(
            notificacionServicio.listarNoLeidas(id));
    }

    /* GET /api/notificaciones/usuario/{id}/contador */
    @Operation(summary = "Contar notificaciones no leídas")
    @GetMapping("/usuario/{id}/contador")
    public ResponseEntity<Map<String, Long>>
            contarNoLeidas(@PathVariable Integer id) {
        long total = notificacionServicio.contarNoLeidas(id);
        return ResponseEntity.ok(Map.of("total", total));
    }

    /* PATCH /api/notificaciones/{id}/leer */
    @Operation(summary = "Marcar notificación como leída")
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Notificacion>
            marcarLeida(@PathVariable Integer id) {
        return ResponseEntity.ok(
            notificacionServicio.marcarLeida(id));
    }

    /* PATCH /api/notificaciones/usuario/{id}/leer-todas */
    @Operation(summary = "Marcar todas como leídas")
    @PatchMapping("/usuario/{id}/leer-todas")
    public ResponseEntity<Map<String, String>>
            marcarTodasLeidas(@PathVariable Integer id) {
        notificacionServicio.marcarTodasLeidas(id);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Todas marcadas como leídas"));
    }

    /* DELETE /api/notificaciones/{id} */
    @Operation(summary = "Eliminar notificación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>>
            eliminar(@PathVariable Integer id) {
        notificacionServicio.eliminar(id);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Notificación eliminada"));
    }
}