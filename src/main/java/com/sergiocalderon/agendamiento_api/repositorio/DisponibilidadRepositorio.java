package com.sergiocalderon.agendamiento_api.repositorio;

import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para operaciones CRUD de Disponibilidad
 */
@Repository
public interface DisponibilidadRepositorio
        extends JpaRepository<Disponibilidad, Integer> {

    // Buscar horarios por fecha
    List<Disponibilidad> findByFecha(LocalDate fecha);

    // Buscar horarios disponibles por fecha
    List<Disponibilidad> findByFechaAndDisponibleTrue(
        LocalDate fecha);

    // Buscar horarios por administrador
    List<Disponibilidad> findByIdAdministrador(
        Integer idAdministrador);

    // Buscar horarios disponibles con cupos libres
@Query("SELECT d FROM Disponibilidad d WHERE d.fecha = :fecha AND d.disponible = true AND d.cuposOcupados < d.cuposTotales")
List<Disponibilidad> findDisponiblesConCupos(@Param("fecha") java.time.LocalDate fecha);
}