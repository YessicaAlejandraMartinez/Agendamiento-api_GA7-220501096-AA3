package com.sergiocalderon.agendamiento_api.repositorio;

import com.sergiocalderon.agendamiento_api.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

//Repositorio JPA para operaciones CRUD de Usuario

@Repository
public interface UsuarioRepositorio 
        extends JpaRepository<Usuario, Integer> {

    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si existe un email registrado
    boolean existsByEmail(String email);
}
