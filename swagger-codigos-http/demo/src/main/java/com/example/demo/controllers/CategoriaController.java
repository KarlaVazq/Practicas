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

import com.example.demo.model.Department;
import com.example.demo.repo.categoriaRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Categorías", description = "Crear, buscar y administrar Categorías")
public class CategoriaController {

    @Autowired
    categoriaRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("categorias")
    @Operation(summary = "Listar categorias")
    @ApiResponse(responseCode = "200", description = "Lista (puede ser vacía: [])")
    public ResponseEntity<List<Department>> findAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("categoria")
    @Operation(summary = "Crear categoria")
    @ApiResponse(responseCode = "201", description = "Creado; encabezado Location con la ruta del recurso")
    public ResponseEntity<Department> add(@RequestBody Department body) {
        body.setId(null);
        Department saved = repo.save(body);
        return ResponseEntity.created(URI.create("/categoria/" + saved.getId())).body(saved);
    }

    @GetMapping("categoria/{id}")
    @Operation(summary = "Buscar categoria por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Department> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("categoria/{id}")
    @Operation(summary = "Eliminar categoria")
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
@PutMapping("categoria/{id}")
    @Operation(summary = "Actualizar categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizada"),
        @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public ResponseEntity<Department> put(@PathVariable String id, @RequestBody Department nuevo) {
        return repo.findById(id).map(c -> {
            c.setNombre(nuevo.getNombre());
            c.setDescripcion(nuevo.getDescripcion());
            c.setTipo(nuevo.getTipo());
            c.setIcono(nuevo.getIcono());
            c.setImagenNavbar(nuevo.getImagenNavbar());
            return ResponseEntity.ok(repo.save(c));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("addProductoCategoria/{productId}/{categoriaId}")
    @Operation(summary = "Asociar un producto con una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asociación creada"),
        @ApiResponse(responseCode = "404", description = "Producto o categoría inexistente", content = @Content)
    })
    public ResponseEntity<Department> addProductoCategoria(@PathVariable String productId, @PathVariable String categoriaId) {
        var producto = repoProducto.findById(productId);
        var categoria = repo.findById(categoriaId);
        if (producto.isEmpty() || categoria.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        categoria.get().getProductos().add(producto.get());
        producto.get().getDeparments().add(categoria.get());
        return ResponseEntity.ok(repo.save(categoria.get()));
    }

    @GetMapping("categoria/producto/{productId}")
    @Operation(summary = "Listar categorías de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorías del producto"),
        @ApiResponse(responseCode = "404", description = "Producto inexistente", content = @Content)
    })
    public ResponseEntity<Set<Department>> byProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(p -> ResponseEntity.ok(p.getDeparments()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
