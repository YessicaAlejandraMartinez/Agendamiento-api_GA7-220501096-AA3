package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import com.sergiocalderon.agendamiento_api.repositorio.DisponibilidadRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio para Disponibilidad
 */
@Service
public class DisponibilidadServicio {

    @Autowired
    private DisponibilidadRepositorio disponibilidadRepositorio;

    // Listar todos los horarios
    public List<Disponibilidad> listarTodos() {
        return disponibilidadRepositorio.findAll();
    }

    // Buscar por ID
    public Optional<Disponibilidad> buscarPorId(Integer id) {
        return disponibilidadRepositorio.findById(id);
    }

    // Listar por fecha
    public List<Disponibilidad> listarPorFecha(LocalDate fecha) {
        return disponibilidadRepositorio.findByFecha(fecha);
    }

    // Listar disponibles por fecha (con cupos libres)
    public List<Disponibilidad> listarDisponiblesPorFecha(LocalDate fecha) {
    // Retorna TODOS los horarios activos de la fecha
    // incluyendo los llenos para mostrarlos deshabilitados
    return disponibilidadRepositorio.findByFechaAndDisponibleTrue(fecha);
}

    // Crear nuevo horario
    public Disponibilidad crear(Disponibilidad disponibilidad) {
        // Inicializar cupos ocupados en 0
        if (disponibilidad.getCuposOcupados() == null) {
            disponibilidad.setCuposOcupados(0);
        }
        // Activo por defecto
        if (disponibilidad.getDisponible() == null) {
            disponibilidad.setDisponible(true);
        }
        return disponibilidadRepositorio.save(disponibilidad);
    }

    // Actualizar horario
    public Disponibilidad actualizar(Integer id,
            Disponibilidad datos) {
        Disponibilidad disp = disponibilidadRepositorio
            .findById(id)
            .orElseThrow(() -> new RuntimeException(
                "Horario no encontrado con ID: " + id));

        disp.setFecha(datos.getFecha());
        disp.setHoraInicio(datos.getHoraInicio());
        disp.setHoraFin(datos.getHoraFin());
        disp.setDisponible(datos.getDisponible());
        disp.setCuposTotales(datos.getCuposTotales());

        return disponibilidadRepositorio.save(disp);
    }

    // Desactivar horario (no eliminar)
    public Disponibilidad desactivar(Integer id) {
        Disponibilidad disp = disponibilidadRepositorio
            .findById(id)
            .orElseThrow(() -> new RuntimeException(
                "Horario no encontrado con ID: " + id));
        disp.setDisponible(false);
        return disponibilidadRepositorio.save(disp);
    }

    // Eliminar horario
    public void eliminar(Integer id) {
        if (!disponibilidadRepositorio.existsById(id)) {
            throw new RuntimeException(
                "Horario no encontrado con ID: " + id);
        }
        disponibilidadRepositorio.deleteById(id);
    }
}