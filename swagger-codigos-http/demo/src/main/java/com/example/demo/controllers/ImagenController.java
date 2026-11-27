package com.example.demo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Image;
import com.example.demo.repo.imagenRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Imágenes", description = "Crear, buscar y administrar Imágenes")
public class ImagenController {

    @Autowired
    imagenRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("imagenes")
    @Operation(summary = "Listar imagenes")
    @ApiResponse(responseCode = "200", description = "Lista (puede ser vacía: [])")
    public ResponseEntity<List<Image>> findAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("imagen")
    @Operation(summary = "Crear imagen")
    @ApiResponse(responseCode = "201", description = "Creado; encabezado Location con la ruta del recurso")
    public ResponseEntity<Image> add(@RequestBody Image body) {
        body.setId(null);
        Image saved = repo.save(body);
        return ResponseEntity.created(URI.create("/imagen/" + saved.getId())).body(saved);
    }

    @GetMapping("imagen/{id}")
    @Operation(summary = "Buscar imagen por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Image> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("imagen/{id}")
    @Operation(summary = "Eliminar imagen")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
@PutMapping("imagen/{id}")
    @Operation(summary = "Actualizar imagen existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizada"),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Image> put(@PathVariable String id, @RequestBody Image nueva) {
        return repo.findById(id).map(i -> {
            i.setUrl(nueva.getUrl());
            i.setPrioridad(nueva.getPrioridad());
            i.setTamanio(nueva.getTamanio());
            i.setResponsiva(nueva.isResponsiva());
            return ResponseEntity.ok(repo.save(i));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("imagen/{productId}")
    @Operation(summary = "Crear imagen para un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada"),
        @ApiResponse(responseCode = "404", description = "Producto inexistente", content = @Content)
    })
    public ResponseEntity<Image> addToProduct(@RequestBody Image body, @PathVariable String productId) {
        return repoProducto.findById(productId).map(p -> {
            body.setId(null);
            body.setProducto(p);
            Image saved = repo.save(body);
            return ResponseEntity.created(URI.create("/imagen/" + saved.getId())).body(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ruta corregida: /imagenes/producto/{id} evita chocar con GET /imagen/{id}
    @GetMapping("imagenes/producto/{productId}")
    @Operation(summary = "Listar imágenes de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imágenes del producto"),
        @ApiResponse(responseCode = "404", description = "Producto inexistente", content = @Content)
    })
    public ResponseEntity<Set<Image>> byProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(p -> ResponseEntity.ok(p.getImagenes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
