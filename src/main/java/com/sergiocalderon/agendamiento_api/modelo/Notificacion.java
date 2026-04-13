package com.sergiocalderon.agendamiento_api.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación del sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "tipo_notificacion")
    private String tipoNotificacion;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "canal_envio")
    private String canalEnvio = "SISTEMA";

    @Column(name = "destinatario", length = 100)
    private String destinatario;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "estado_envio")
    private String estadoEnvio = "PENDIENTE";

    @Column(name = "id_cita")
    private Integer idCita;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "leida")
    private Boolean leida = false;
}
