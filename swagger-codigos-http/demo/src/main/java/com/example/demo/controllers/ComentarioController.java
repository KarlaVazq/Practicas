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

import com.example.demo.model.Comment;
import com.example.demo.repo.comentarioRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Comentarios", description = "Crear, buscar y administrar Comentarios")
public class ComentarioController {

    @Autowired
    comentarioRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("comentarios")
    @Operation(summary = "Listar comentarios")
    @ApiResponse(responseCode = "200", description = "Lista (puede ser vacía: [])")
    public ResponseEntity<List<Comment>> findAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("comentario")
    @Operation(summary = "Crear comentario")
    @ApiResponse(responseCode = "201", description = "Creado; encabezado Location con la ruta del recurso")
    public ResponseEntity<Comment> add(@RequestBody Comment body) {
        body.setId(null);
        Comment saved = repo.save(body);
        return ResponseEntity.created(URI.create("/comentario/" + saved.getId())).body(saved);
    }

    @GetMapping("comentario/{id}")
    @Operation(summary = "Buscar comentario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Comment> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("comentario/{id}")
    @Operation(summary = "Eliminar comentario")
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
@PutMapping("comentario/{id}")
    @Operation(summary = "Actualizar comentario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado"),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Comment> put(@PathVariable String id, @RequestBody Comment nuevo) {
        return repo.findById(id).map(c -> {
            c.setUsername(nuevo.getUsername());
            c.setComentario_corto(nuevo.getComentario_corto());
            c.setEstrellas(nuevo.getEstrellas());
            c.setComentario_largo(nuevo.getComentario_largo());
            c.setImagen1(nuevo.getImagen1());
            c.setImagen2(nuevo.getImagen2());
            return ResponseEntity.ok(repo.save(c));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("comentario/{productId}")
    @Operation(summary = "Crear comentario para un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creado"),
        @ApiResponse(responseCode = "404", description = "Producto inexistente", content = @Content)
    })
    public ResponseEntity<Comment> addToProduct(@RequestBody Comment body, @PathVariable String productId) {
        return repoProducto.findById(productId).map(p -> {
            body.setId(null);
            body.setProducto(p);
            Comment saved = repo.save(body);
            return ResponseEntity.created(URI.create("/comentario/" + saved.getId())).body(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("comentarios/{productId}")
    @Operation(summary = "Listar comentarios de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentarios del producto"),
        @ApiResponse(responseCode = "404", description = "Producto inexistente", content = @Content)
    })
    public ResponseEntity<Set<Comment>> byProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(p -> ResponseEntity.ok(p.getComentarios()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
