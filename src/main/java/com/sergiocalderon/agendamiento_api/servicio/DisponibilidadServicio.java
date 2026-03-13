package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Disponibilidad;
import com.sergiocalderon.agendamiento_api.repositorio.DisponibilidadRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisponibilidadServicio {

    private final DisponibilidadRepositorio repositorio;

    public DisponibilidadServicio(DisponibilidadRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Disponibilidad> listar() {
        return repositorio.findAll();
    }

    public Disponibilidad guardar(Disponibilidad disponibilidad) {
        return repositorio.save(disponibilidad);
    }

    public Disponibilidad buscarPorId(Integer id) {
        return repositorio.findById(id).orElse(null);
    }

    public void eliminar(Integer id) {
        repositorio.deleteById(id);
    }
}