package com.sergiocalderon.agendamiento_api.controlador;

import com.sergiocalderon.agendamiento_api.modelo.Vestido;
import com.sergiocalderon.agendamiento_api.repositorio.VestidoRepositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador REST para gestión del catálogo de vestidos
 * Base URL: /api/vestidos
 */
@Tag(name = "Vestidos", description = "Gestión del catálogo de vestidos")
@RestController
@RequestMapping("/api/vestidos")
@CrossOrigin(origins = "*")
public class VestidoControlador {

    @Autowired
    private VestidoRepositorio vestidoRepositorio;

    @Value("${app.upload.dir:uploads/catalogo}")
    private String uploadDir;

    /* GET /api/vestidos — Listar todos los activos */
    @Operation(summary = "Listar vestidos activos del catálogo")
    @GetMapping
    public ResponseEntity<List<Vestido>> listarTodos() {
        return ResponseEntity.ok(
                vestidoRepositorio.findByActivoTrue());
    }

    /* GET /api/vestidos/todos — Listar todos incluyendo inactivos */
    @Operation(summary = "Listar todos incluyendo inactivos (admin)")
    @GetMapping("/todos")
    public ResponseEntity<List<Vestido>> listarTodosAdmin() {
        return ResponseEntity.ok(
                vestidoRepositorio.findAll());
    }

    /* GET /api/vestidos/{id} — Buscar por ID */
    @Operation(summary = "Buscar vestido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Integer id) {
        return vestidoRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* POST /api/vestidos — Crear vestido con imagen */
    @Operation(summary = "Crear vestido con imagen", description = "Sube imagen al servidor y crea registro en BD")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> crear(
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            Vestido vestido = new Vestido();
            vestido.setNombre(nombre);
            vestido.setCategoria(categoria);
            vestido.setDescripcion(descripcion);
            vestido.setActivo(true);

            /* Guardar imagen si viene */
            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = guardarImagen(imagen);
                vestido.setImagenUrl(nombreArchivo);
            }

            Vestido creado = vestidoRepositorio.save(vestido);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* PUT /api/vestidos/{id} — Actualizar vestido */
    @Operation(summary = "Actualizar vestido")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        return vestidoRepositorio.findById(id)
                .map(vestido -> {
                    try {
                        vestido.setNombre(nombre);
                        vestido.setCategoria(categoria);
                        vestido.setDescripcion(descripcion);

                        if (imagen != null && !imagen.isEmpty()) {
                            /* Eliminar imagen anterior si existe */
                            eliminarImagenAnterior(
                                    vestido.getImagenUrl());
                            String nombreArchivo = guardarImagen(imagen);
                            vestido.setImagenUrl(nombreArchivo);
                        }

                        return ResponseEntity.ok(
                                vestidoRepositorio.save(vestido));
                    } catch (Exception e) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", e.getMessage()));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /* PATCH /api/vestidos/{id}/desactivar — Desactivar */
    @Operation(summary = "Desactivar vestido del catálogo")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(
            @PathVariable Integer id) {
        return vestidoRepositorio.findById(id)
                .map(vestido -> {
                    vestido.setActivo(false);
                    return ResponseEntity.ok(
                            vestidoRepositorio.save(vestido));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /* DELETE /api/vestidos/{id} — Eliminar */
    @Operation(summary = "Eliminar vestido")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id) {
        return vestidoRepositorio.findById(id)
                .map(vestido -> {
                    eliminarImagenAnterior(vestido.getImagenUrl());
                    vestidoRepositorio.deleteById(id);
                    return ResponseEntity.ok(
                            Map.of("mensaje",
                                    "Vestido eliminado correctamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /* Guarda la imagen en disco y retorna el nombre */
    private String guardarImagen(MultipartFile archivo)
            throws IOException {
        /* Crear directorio si no existe */
        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        /* Generar nombre único para evitar conflictos */
        String extension = obtenerExtension(
                archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID()
                .toString() + "." + extension;

        /* Guardar el archivo */
        Path rutaArchivo = dirPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo,
                StandardCopyOption.REPLACE_EXISTING);

        return nombreArchivo;
    }

    /* Elimina la imagen anterior del disco */
    private void eliminarImagenAnterior(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty())
            return;
        try {
            Path ruta = Paths.get(uploadDir)
                    .resolve(nombreArchivo);
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar imagen: "
                    + e.getMessage());
        }
    }

    /* Extrae la extensión del nombre de archivo */
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null)
            return "jpg";
        int punto = nombreArchivo.lastIndexOf('.');
        return punto > 0
                ? nombreArchivo.substring(punto + 1).toLowerCase()
                : "jpg";
    }
}