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

    // Listar todas las citas
    public List<Cita> listarTodas() {
        return citaRepositorio.findAll();
    }

    // Buscar cita por ID
    public Optional<Cita> buscarPorId(Integer id) {
        return citaRepositorio.findById(id);
    }

    // Listar citas por cliente
    public List<Cita> listarPorCliente(Integer idCliente) {
        return citaRepositorio.findByIdCliente(idCliente);
    }

    // Listar citas por estado
    public List<Cita> listarPorEstado(String estado) {
        return citaRepositorio.findByEstado(estado);
    }

    // Listar citas por fecha
    public List<Cita> listarPorFecha(LocalDate fecha) {
        return citaRepositorio.findByFechaCita(fecha);
    }

    // Crear nueva cita — ocupa el cupo del horario
    @Transactional
    public Cita crearCita(Cita cita) {

        /* Verificar que el horario existe */
        if (cita.getIdDisponibilidad() != null) {
            Disponibilidad disp = disponibilidadRepositorio
                    .findById(cita.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException(
                            "Horario no encontrado"));

            /* Verificar que aún hay cupos */
            if (disp.getCuposOcupados() >= disp.getCuposTotales()) {
                throw new RuntimeException(
                        "Este horario ya no tiene cupos disponibles");
            }

            /* Incrementar cupos ocupados */
            disp.setCuposOcupados(disp.getCuposOcupados() + 1);

            /* Si se llenó completamente, marcarlo como no disponible */
            if (disp.getCuposOcupados() >= disp.getCuposTotales()) {
                disp.setDisponible(false);
            }

            disponibilidadRepositorio.save(disp);
        }

        /* Estado inicial PENDIENTE */
        cita.setEstado("PENDIENTE");
        return citaRepositorio.save(cita);
    }

    // Modificar cita
    @Transactional
    public Cita modificarCita(Integer id, Cita citaActualizada) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden modificar citas en estado PENDIENTE");
        }

        /* Liberar el cupo del horario anterior */
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

        /* Ocupar el nuevo horario */
        if (citaActualizada.getIdDisponibilidad() != null) {
            Disponibilidad dispNuevo = disponibilidadRepositorio
                    .findById(citaActualizada.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException(
                            "Horario no encontrado"));

            if (dispNuevo.getCuposOcupados() >= dispNuevo.getCuposTotales()) {
                throw new RuntimeException(
                        "Este horario ya no tiene cupos disponibles");
            }

            dispNuevo.setCuposOcupados(dispNuevo.getCuposOcupados() + 1);
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
        cita.setIdDisponibilidad(citaActualizada.getIdDisponibilidad());

        return citaRepositorio.save(cita);
    }

    // Confirmar cita
    public Cita confirmarCita(Integer id) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden confirmar citas en estado PENDIENTE");
        }

        cita.setEstado("CONFIRMADA");
        return citaRepositorio.save(cita);
    }

    // Cancelar cita — libera el cupo del horario
    @Transactional
    public Cita cancelarCita(Integer id, String motivo) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        if ("CANCELADA".equals(cita.getEstado())) {
            throw new RuntimeException("La cita ya está cancelada");
        }

        /* Liberar el cupo del horario */
        if (cita.getIdDisponibilidad() != null) {
            disponibilidadRepositorio
                    .findById(cita.getIdDisponibilidad())
                    .ifPresent(disp -> {
                        if (disp.getCuposOcupados() > 0) {
                            disp.setCuposOcupados(
                                    disp.getCuposOcupados() - 1);
                            /* Reactivar el horario si tenía cupos */
                            disp.setDisponible(true);
                            disponibilidadRepositorio.save(disp);
                        }
                    });
        }

        cita.setEstado("CANCELADA");
        cita.setMotivoCancelacion(motivo);
        return citaRepositorio.save(cita);
    }

    // Eliminar cita — libera el cupo
    @Transactional
    public void eliminarCita(Integer id) {
        Cita cita = citaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cita no encontrada con ID: " + id));

        /* Liberar cupo si la cita no estaba cancelada */
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