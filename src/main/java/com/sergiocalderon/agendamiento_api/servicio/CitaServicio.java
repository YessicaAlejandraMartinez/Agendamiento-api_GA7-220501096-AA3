package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Cita;
import com.sergiocalderon.agendamiento_api.repositorio.CitaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class CitaServicio {

    @Autowired
    private CitaRepositorio citaRepositorio;

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

    // Crear nueva cita
    public Cita crearCita(Cita cita) {
        // Estado inicial siempre PENDIENTE
        cita.setEstado("PENDIENTE");
        return citaRepositorio.save(cita);
    }

    // Modificar cita existente
    public Cita modificarCita(Integer id, Cita citaActualizada) {
        Cita cita = citaRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException(
                "Cita no encontrada con ID: " + id));

        // Solo se puede modificar si está PENDIENTE
        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                "Solo se pueden modificar citas en estado PENDIENTE");
        }

        cita.setFechaCita(citaActualizada.getFechaCita());
        cita.setHoraInicio(citaActualizada.getHoraInicio());
        cita.setHoraFin(citaActualizada.getHoraFin());
        cita.setTipoEvento(citaActualizada.getTipoEvento());
        cita.setMotivoCita(citaActualizada.getMotivoCita());

        return citaRepositorio.save(cita);
    }

    // Cancelar cita
    public Cita cancelarCita(Integer id, String motivo) {
        Cita cita = citaRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException(
                "Cita no encontrada con ID: " + id));

        if ("CANCELADA".equals(cita.getEstado())) {
            throw new RuntimeException(
                "La cita ya está cancelada");
        }

        cita.setEstado("CANCELADA");
        cita.setMotivoCancelacion(motivo);
        return citaRepositorio.save(cita);
    }

    // Eliminar cita
    public void eliminarCita(Integer id) {
        if (!citaRepositorio.existsById(id)) {
            throw new RuntimeException(
                "Cita no encontrada con ID: " + id);
        }
        citaRepositorio.deleteById(id);
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
}