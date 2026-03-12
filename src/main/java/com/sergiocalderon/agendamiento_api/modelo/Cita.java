package com.sergiocalderon.agendamiento_api.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa una cita agendada en el atelier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cita")
public class Cita {

    // Identificador único de la cita
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    // Fecha de la cita
    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    // Hora de inicio
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    // Hora de fin
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    // Duración en minutos
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    // Tipo de evento
    @Column(name = "tipo_evento", length = 50)
    private String tipoEvento;

    // Motivo de la cita
    @Column(name = "motivo_cita", length = 500)
    private String motivoCita;

    // Estado de la cita
    @Column(name = "estado", length = 20)
    private String estado;

    // Motivo de cancelación
    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    // ID del cliente que agendó
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    // ID de la disponibilidad seleccionada
    @Column(name = "id_disponibilidad")
    private Integer idDisponibilidad;
}