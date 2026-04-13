package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Cita;
import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import com.sergiocalderon.agendamiento_api.repositorio.CitaRepositorio;
import com.sergiocalderon.agendamiento_api.repositorio.DisponibilidadRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CitaServicio {

    @Autowired
    private CitaRepositorio citaRepositorio;

    @Autowired
    private DisponibilidadRepositorio disponibilidadRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    public List<Cita> listarTodas() {
        return citaRepositorio.findAll();
    }

    public Optional<Cita> buscarPorId(Integer id) {
        return citaRepositorio.findById(id);
    }

    public List<Cita> listarPorCliente(Integer idCliente) {
        return citaRepositorio.findByIdCliente(idCliente);
    }

    public List<Cita> listarPorEstado(String estado) {
        return citaRepositorio.findByEstado(estado);
    }

    public List<Cita> listarPorFecha(LocalDate fecha) {
        return citaRepositorio.findByFechaCita(fecha);
    }

    /* ── CREAR CITA ── */
    @Transactional
    public Cita crearCita(Cita cita) {

        if (cita.getIdDisponibilidad() != null) {
            Disponibilidad disp = disponibilidadRepositorio
                    .findById(cita.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException(
                            "Horario no encontrado"));

            if (disp.getCuposOcupados() >= disp.getCuposTotales()) {
                throw new RuntimeException(
                        "Este horario ya no tiene cupos disponibles");
            }

            disp.setCuposOcupados(disp.getCuposOcupados() + 1);

            if (disp.getCuposOcupados() >= disp.getCuposTotales()) {
                disp.setDisponible(false);
            }

            disponibilidadRepositorio.save(disp);
        }

        cita.setEstado("PENDIENTE");
        Cita creado = citaRepositorio.save(cita);

        // Notificar al cliente
        notificacionServicio.notificarCitaAgendada(
                creado.getIdCliente(),
                creado.getIdCita(),
                creado.getFechaCita().toString(),
                creado.getHoraInicio().toString());

        // Notificar al admin (ID 1 por defecto)
        notificacionServicio.notificarAdminNuevaCita(
                1,
                creado.getIdCita(),
                creado.getIdCliente(),
                creado.getFechaCita().toString(),
                creado.getHoraInicio().toString());

        return creado;
    }

    /* ── MODIFICAR CITA ── */
    @Transactional
    public Cita modificarCita(Integer id, Cita citaActualizada) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden modificar citas en estado PENDIENTE");
        }

        // Liberar cupo anterior
        if (cita.getIdDisponibilidad() != null) {
            disponibilidadRepositorio
                    .findById(cita.getIdDisponibilidad())
                    .ifPresent(dispAnterior -> {
                        if (dispAnterior.getCuposOcupados() > 0) {
                            dispAnterior.setCuposOcupados(
                                    dispAnterior.getCuposOcupados() - 1);
                            dispAnterior.setDisponible(true);
                            disponibilidadRepositorio.save(dispAnterior);
                        }
                    });
        }

        // Ocupar nuevo cupo
        if (citaActualizada.getIdDisponibilidad() != null) {
            Disponibilidad dispNuevo = disponibilidadRepositorio
                    .findById(citaActualizada.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException(
                            "Horario no encontrado"));

            if (dispNuevo.getCuposOcupados() >= dispNuevo.getCuposTotales()) {
                throw new RuntimeException(
                        "Este horario ya no tiene cupos disponibles");
            }

            dispNuevo.setCuposOcupados(
                    dispNuevo.getCuposOcupados() + 1);
            if (dispNuevo.getCuposOcupados() >= dispNuevo.getCuposTotales()) {
                dispNuevo.setDisponible(false);
            }
            disponibilidadRepositorio.save(dispNuevo);
        }

        cita.setFechaCita(citaActualizada.getFechaCita());
        cita.setHoraInicio(citaActualizada.getHoraInicio());
        cita.setHoraFin(citaActualizada.getHoraFin());
        cita.setTipoEvento(citaActualizada.getTipoEvento());
        cita.setMotivoCita(citaActualizada.getMotivoCita());
        cita.setIdDisponibilidad(
                citaActualizada.getIdDisponibilidad());

        return citaRepositorio.save(cita);
    }

    /* ── CONFIRMAR CITA ── */
    public Cita confirmarCita(Integer id) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden confirmar citas PENDIENTE");
        }

        cita.setEstado("CONFIRMADA");
        Cita confirmada = citaRepositorio.save(cita);

        // Notificar al cliente
        notificacionServicio.notificarCitaConfirmada(
                confirmada.getIdCliente(),
                id,
                confirmada.getFechaCita().toString(),
                confirmada.getHoraInicio().toString());

        return confirmada;
    }

    /* ── CANCELAR CITA ── */
    @Transactional
    public Cita cancelarCita(Integer id, String motivo) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        if ("CANCELADA".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "La cita ya está cancelada");
        }

        // Liberar cupo
        if (cita.getIdDisponibilidad() != null) {
            disponibilidadRepositorio
                    .findById(cita.getIdDisponibilidad())
                    .ifPresent(disp -> {
                        if (disp.getCuposOcupados() > 0) {
                            disp.setCuposOcupados(
                                    disp.getCuposOcupados() - 1);
                            disp.setDisponible(true);
                            disponibilidadRepositorio.save(disp);
                        }
                    });
        }

        cita.setEstado("CANCELADA");
        cita.setMotivoCancelacion(motivo);
        Cita cancelada = citaRepositorio.save(cita);

        // Notificar al cliente
        notificacionServicio.notificarCitaCancelada(
                cancelada.getIdCliente(),
                id,
                cancelada.getFechaCita().toString(),
                motivo);

        return cancelada;
    }

    /* ── ELIMINAR CITA ── */
    @Transactional
    public void eliminarCita(Integer id) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        // Liberar cupo si no estaba cancelada
        if (!"CANCELADA".equals(cita.getEstado()) &&
                cita.getIdDisponibilidad() != null) {
            disponibilidadRepositorio
                    .findById(cita.getIdDisponibilidad())
                    .ifPresent(disp -> {
                        if (disp.getCuposOcupados() > 0) {
                            disp.setCuposOcupados(
                                    disp.getCuposOcupados() - 1);
                            disp.setDisponible(true);
                            disponibilidadRepositorio.save(disp);
                        }
                    });
        }

        citaRepositorio.deleteById(id);
    }
}