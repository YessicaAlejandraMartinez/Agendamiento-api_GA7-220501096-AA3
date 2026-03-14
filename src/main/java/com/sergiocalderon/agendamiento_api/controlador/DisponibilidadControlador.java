package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import com.sergiocalderon.agendamiento_api.servicio.DisponibilidadServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidad")
public class DisponibilidadControlador {

    private final DisponibilidadServicio servicio;

    public DisponibilidadControlador(DisponibilidadServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Disponibilidad> listar() {
        return servicio.listar();
    }

    @PostMapping
    public Disponibilidad guardar(@RequestBody Disponibilidad disponibilidad) {
        return servicio.guardar(disponibilidad);
    }

    @GetMapping("/{id}")
    public Disponibilidad buscar(@PathVariable Integer id) {
        return servicio.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
    }
}