package com.sergiocalderon.agendamiento_api.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa un vestido del catálogo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vestido")
public class Vestido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vestido")
    private Integer idVestido;

    /* Nombre del vestido */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /* Categoría del vestido */
    @Column(name = "categoria", length = 50)
    private String categoria;

    /* Descripción del vestido */
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /* Nombre del archivo de imagen guardado */
    @Column(name = "imagen_url", length = 300)
    private String imagenUrl;

    /* Si está activo en el catálogo */
    @Column(name = "activo")
    private Boolean activo = true;
}