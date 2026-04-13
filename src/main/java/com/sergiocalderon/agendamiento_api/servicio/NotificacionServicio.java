package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Notificacion;
import com.sergiocalderon.agendamiento_api.repositorio.NotificacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para crear y gestionar notificaciones del sistema
 */
@Service
public class NotificacionServicio {

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    /* Crear notificación genérica */
    public Notificacion crear(String tipo, String titulo,
            String mensaje, Integer idUsuario, Integer idCita) {

        Notificacion n = new Notificacion();
        n.setTipoNotificacion(tipo);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setCanalEnvio("SISTEMA");
        n.setIdUsuario(idUsuario);
        n.setIdCita(idCita);
        n.setFechaProgramada(LocalDateTime.now());
        n.setFechaEnvio(LocalDateTime.now());
        n.setEstadoEnvio("ENVIADO");
        n.setLeida(false);
        return notificacionRepositorio.save(n);
    }

    /* Notificación al CLIENTE cuando agenda una cita */
    public void notificarCitaAgendada(Integer idCliente,
            Integer idCita, String fecha, String hora) {
        crear("CONFIRMACION",
            "Cita agendada exitosamente",
            "Tu cita para el " + fecha + " a las " + hora +
            " ha sido registrada. Estado: PENDIENTE.",
            idCliente, idCita);
    }

    /* Notificación al CLIENTE cuando el admin confirma */
    public void notificarCitaConfirmada(Integer idCliente,
            Integer idCita, String fecha, String hora) {
        crear("CONFIRMACION",
            "¡Tu cita fue confirmada!",
            "Tu cita del " + fecha + " a las " + hora +
            " ha sido CONFIRMADA por el atelier.",
            idCliente, idCita);
    }

    /* Notificación al CLIENTE cuando se cancela */
    public void notificarCitaCancelada(Integer idCliente,
            Integer idCita, String fecha, String motivo) {
        crear("CANCELACION",
            "Cita cancelada",
            "Tu cita del " + fecha + " ha sido cancelada." +
            (motivo != null ? " Motivo: " + motivo : ""),
            idCliente, idCita);
    }

    /* Notificación al ADMIN cuando un cliente agenda */
    public void notificarAdminNuevaCita(Integer idAdmin,
            Integer idCita, Integer idCliente,
            String fecha, String hora) {
        crear("CONFIRMACION",
            "Nueva cita agendada",
            "El cliente #" + idCliente + " agendó una cita" +
            " para el " + fecha + " a las " + hora + ".",
            idAdmin, idCita);
    }

    /* Listar todas las notificaciones de un usuario */
    public List<Notificacion> listarPorUsuario(
            Integer idUsuario) {
        return notificacionRepositorio
            .findByIdUsuarioOrderByFechaProgramadaDesc(
                idUsuario);
    }

    /* Listar solo no leídas */
    public List<Notificacion> listarNoLeidas(
            Integer idUsuario) {
        return notificacionRepositorio
            .findByIdUsuarioAndLeidaFalse(idUsuario);
    }

    /* Contar no leídas */
    public long contarNoLeidas(Integer idUsuario) {
        return notificacionRepositorio
            .countByIdUsuarioAndLeidaFalse(idUsuario);
    }

    /* Marcar una como leída */
    public Notificacion marcarLeida(Integer id) {
        return notificacionRepositorio.findById(id)
            .map(n -> {
                n.setLeida(true);
                return notificacionRepositorio.save(n);
            })
            .orElseThrow(() -> new RuntimeException(
                "Notificación no encontrada"));
    }

    /* Marcar todas como leídas */
    public void marcarTodasLeidas(Integer idUsuario) {
        List<Notificacion> noLeidas = notificacionRepositorio
            .findByIdUsuarioAndLeidaFalse(idUsuario);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepositorio.saveAll(noLeidas);
    }

    /* Eliminar una notificación */
    public void eliminar(Integer id) {
        notificacionRepositorio.deleteById(id);
    }
}
