package com.sergiocalderon.agendamiento_api.repositorio;

import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibilidadRepositorio extends JpaRepository<Disponibilidad, Integer> {
}