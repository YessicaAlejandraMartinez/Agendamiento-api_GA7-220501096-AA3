package com.sergiocalderon.agendamiento_api.servicio;

import com.sergiocalderon.agendamiento_api.modelo.Usuario;
import com.sergiocalderon.agendamiento_api.repositorio.UsuarioRepositorio;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para Usuario
 */
@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EntityManager entityManager;

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }

    // Buscar usuario por email
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    // Registrar nuevo usuario — inserta también en cliente si aplica
    @Transactional
    public Usuario registrar(Usuario usuario) {

        // Validar email duplicado
        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException(
                "Ya existe un usuario con el email: " +
                usuario.getEmail());
        }

        // Guardar en tabla usuario
        Usuario nuevo = usuarioRepositorio.save(usuario);

        // Si es CLIENTE, insertar también en tabla cliente
        if (Usuario.TipoUsuario.CLIENTE.equals(
                usuario.getTipoUsuario())) {
            entityManager.createNativeQuery(
                "INSERT INTO cliente " +
                "(id_cliente, direccion, ciudad, numero_citas) " +
                "VALUES (?, '', '', 0)")
                .setParameter(1, nuevo.getIdUsuario())
                .executeUpdate();
        }

        return nuevo;
    }

    // Actualizar usuario existente
@Transactional
public Usuario actualizar(Integer id,
        Usuario usuarioActualizado) {
    Usuario usuario = usuarioRepositorio.findById(id)
        .orElseThrow(() -> new RuntimeException(
            "Usuario no encontrado con ID: " + id));

    // Guardar el tipo anterior para detectar cambio de rol
    Usuario.TipoUsuario tipoAnterior = usuario.getTipoUsuario();

    // Actualizar nombre si viene en la petición
    if (usuarioActualizado.getNombre() != null) {
        usuario.setNombre(usuarioActualizado.getNombre());
    }

    // Actualizar apellido si viene en la petición
    if (usuarioActualizado.getApellido() != null) {
        usuario.setApellido(usuarioActualizado.getApellido());
    }

    // Actualizar teléfono si viene en la petición
    if (usuarioActualizado.getTelefono() != null) {
        usuario.setTelefono(usuarioActualizado.getTelefono());
    }

    // Actualizar email si viene en la petición
    if (usuarioActualizado.getEmail() != null) {
        if (!usuarioActualizado.getEmail().equals(
                usuario.getEmail()) &&
            usuarioRepositorio.existsByEmail(
                usuarioActualizado.getEmail())) {
            throw new RuntimeException(
                "Ya existe un usuario con el email: " +
                usuarioActualizado.getEmail());
        }
        usuario.setEmail(usuarioActualizado.getEmail());
    }

    // Actualizar contraseña si viene en la petición
    if (usuarioActualizado.getContrasena() != null) {
        usuario.setContrasena(
            usuarioActualizado.getContrasena());
    }

    // Actualizar tipo de usuario si viene en la petición
    if (usuarioActualizado.getTipoUsuario() != null) {
        Usuario.TipoUsuario tipoNuevo =
            usuarioActualizado.getTipoUsuario();
        usuario.setTipoUsuario(tipoNuevo);

        // Si cambia a ADMIN y no tenía rol ADMIN antes
        if (Usuario.TipoUsuario.ADMIN.equals(tipoNuevo) &&
            !Usuario.TipoUsuario.ADMIN.equals(tipoAnterior)) {

            // Verificar que no exista ya en personal_administrativo
            Long existe = (Long) entityManager
                .createNativeQuery(
                    "SELECT COUNT(*) FROM personal_administrativo " +
                    "WHERE id_administrador = ?")
                .setParameter(1, id)
                .getSingleResult();

            if (existe == 0) {
                entityManager.createNativeQuery(
                    "INSERT INTO personal_administrativo " +
                    "(id_administrador, cargo, permisos) " +
                    "VALUES (?, 'Administrador', " +
                    "'{\"citas\": true, \"usuarios\": true}')")
                    .setParameter(1, id)
                    .executeUpdate();
            }
        }

        // Si cambia a CLIENTE y no tenía rol CLIENTE antes
        if (Usuario.TipoUsuario.CLIENTE.equals(tipoNuevo) &&
            !Usuario.TipoUsuario.CLIENTE.equals(tipoAnterior)) {

            // Verificar que no exista ya en cliente
            Long existe = (Long) entityManager
                .createNativeQuery(
                    "SELECT COUNT(*) FROM cliente " +
                    "WHERE id_cliente = ?")
                .setParameter(1, id)
                .getSingleResult();

            if (existe == 0) {
                entityManager.createNativeQuery(
                    "INSERT INTO cliente " +
                    "(id_cliente, direccion, ciudad, " +
                    "numero_citas) VALUES (?, '', '', 0)")
                    .setParameter(1, id)
                    .executeUpdate();
            }
        }
    }

    return usuarioRepositorio.save(usuario);
}

    // Eliminar usuario
    public void eliminar(Integer id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException(
                "Usuario no encontrado con ID: " + id);
        }
        usuarioRepositorio.deleteById(id);
    }

    // Login - validar credenciales
    public Optional<Usuario> login(String email,
            String contrasena) {
        return usuarioRepositorio.findByEmail(email)
            .filter(u -> u.getContrasena().equals(contrasena));
    }
}
