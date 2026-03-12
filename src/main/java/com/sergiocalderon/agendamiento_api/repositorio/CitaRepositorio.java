package com.sergiocalderon.agendamiento_api.repositorio;

import com.sergiocalderon.agendamiento_api.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepositorio 
        extends JpaRepository<Cita, Integer> {

    // Buscar citas por cliente
    List<Cita> findByIdCliente(Integer idCliente);

    // Buscar citas por estado
    List<Cita> findByEstado(String estado);

    // Buscar citas por fecha
    List<Cita> findByFechaCita(LocalDate fechaCita);

    // Buscar citas por cliente y estado
    List<Cita> findByIdClienteAndEstado(
        Integer idCliente, String estado);
}