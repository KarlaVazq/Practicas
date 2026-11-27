package com.example.demo.producto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Producto;
import com.example.demo.repo.ProductoRepo;

@RestController
public class ProductoController {

    @Autowired
    ProductoRepo repo;

    // Lista de productos (para comprobar altas y bajas)
    @GetMapping("/productos")
    public List<Producto> findAll() {
        return repo.findAll();
    }

    // Paso 4: POST crea un registro; el id lo genera la base de datos
    @PostMapping("/producto")
    public Producto addProducto(@RequestBody Producto producto) {
        return repo.save(producto);
    }

    // Paso 3: GET por id
    @GetMapping("/producto/{id}")
    public Optional<Producto> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    // Paso 6: PUT reemplaza los datos del producto indicado
    @PutMapping("/producto/{id}")
    public Producto putProduct(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        return repo.save(producto);
    }

    // Paso 8: DELETE elimina el registro
    @DeleteMapping("/producto/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
