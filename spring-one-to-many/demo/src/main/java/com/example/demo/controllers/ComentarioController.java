package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Comment;
import com.example.demo.model.Product;
import com.example.demo.repo.comentarioRepo;
import com.example.demo.repo.productoRepo;

@RestController
public class ComentarioController {

    @Autowired
    comentarioRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("comentarios")
    public List<Comment> findAllComentarios() {
        return repo.findAll();
    }

    // Este es el endpoint que asigna el producto al comentario (Comment.producto)
    @PostMapping("comentario/{productId}")
    public Comment addComentario(@RequestBody Comment comentario, @PathVariable String productId) {
        Optional<Product> p = repoProducto.findById(productId);
        if (p.isPresent()) {
            comentario.setProducto(p.get());
        }
        return repo.save(comentario);
    }

    @GetMapping("comentarios/{productId}")
    public Set<Comment> getCommentsByProduct(@PathVariable String productId) {
        Optional<Product> p = repoProducto.findById(productId);
        if (p.isPresent()) {
            return p.get().getComentarios();
        }
        return null;
    }

    @GetMapping("comentario/{id}")
    public Optional<Comment> getById(@PathVariable String id) {
        return repo.findById(id);
    }

    @PutMapping("comentario/{id}")
    public Comment putComentario(@PathVariable String id, @RequestBody Comment comentario) {
        comentario.setId(id);
        // Conserva el producto actual: si no, la llave foránea quedaría en NULL
        repo.findById(id).ifPresent(actual -> comentario.setProducto(actual.getProducto()));
        return repo.save(comentario);
    }

    @DeleteMapping("comentario/{id}")
    public String deleteComentario(@PathVariable String id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
