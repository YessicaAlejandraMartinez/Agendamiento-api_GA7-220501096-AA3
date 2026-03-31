package com.sergiocalderon.agendamiento_api.repositorio;

import com.sergiocalderon.agendamiento_api.modelo.Vestido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para operaciones CRUD de Vestido
 */
@Repository
public interface VestidoRepositorio
        extends JpaRepository<Vestido, Integer> {

    /* Listar vestidos activos */
    List<Vestido> findByActivoTrue();

    /* Listar por categoría */
    List<Vestido> findByCategoriaAndActivoTrue(String categoria);
}
