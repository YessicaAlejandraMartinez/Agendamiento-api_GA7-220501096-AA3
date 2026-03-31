package com.sergiocalderon.agendamiento_api.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa un horario disponible
 * para agendar citas en el atelier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "disponibilidad")
public class Disponibilidad {

    // Identificador único
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Integer idDisponibilidad;

    // Fecha del horario disponible
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    // Hora de inicio del bloque
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    // Hora de fin del bloque
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    // Si el horario está activo para ser agendado
    @Column(name = "disponible")
    private Boolean disponible;

    // Cupos totales del bloque horario
    @Column(name = "cupos_totales")
    private Integer cuposTotales;

    // Cupos ya ocupados
    @Column(name = "cupos_ocupados")
    private Integer cuposOcupados;

    // ID del administrador que creó el horario
    @Column(name = "id_administrador")
    private Integer idAdministrador;
}