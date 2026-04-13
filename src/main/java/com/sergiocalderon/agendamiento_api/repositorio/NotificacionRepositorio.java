package com.sergiocalderon.agendamiento_api.repositorio;

import com.sergiocalderon.agendamiento_api.modelo.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepositorio
        extends JpaRepository<Notificacion, Integer> {

    /* Notificaciones de un usuario ordenadas por fecha */
    List<Notificacion> findByIdUsuarioOrderByFechaProgramadaDesc(
        Integer idUsuario);

    /* Solo las no leídas de un usuario */
    List<Notificacion> findByIdUsuarioAndLeidaFalse(
        Integer idUsuario);

    /* Contar no leídas */
    long countByIdUsuarioAndLeidaFalse(Integer idUsuario);
}
