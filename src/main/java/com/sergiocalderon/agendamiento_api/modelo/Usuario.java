package com.sergiocalderon.agendamiento_api.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

//Entidad que representa un usuario del sistema

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    // Identificador único del usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Nombre del usuario
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // Apellido del usuario
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    // Correo electrónico único
    @Column(name = "email", nullable = false, unique = true, 
            length = 150)
    private String email;

    // Contraseña del usuario
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    // Teléfono de contacto
    @Column(name = "telefono", length = 20)
    private String telefono;

    // Rol del usuario: ADMIN o CLIENTE
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;

    // Enum para el tipo de usuario
    public enum TipoUsuario {
        ADMIN, CLIENTE
    }
}
