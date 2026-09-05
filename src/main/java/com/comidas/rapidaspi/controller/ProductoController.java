
    package com.comidas.rapidaspi.controller;

import com.comidas.rapidaspi.model.Producto;
import com.comidas.rapidaspi.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permite conexiones desde frontend o clientes externos
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 1. Obtener todos los productos (GET)
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // 2. Obtener un producto por ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(producto -> ResponseEntity.ok().body(producto))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Crear un nuevo producto (POST)
    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // 4. Actualizar un producto existente (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoDetalles.getNombre());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setDisponible(productoDetalles.getDisponible());
            producto.setImagenUrl(productoDetalles.getImagenUrl());
            Producto actualizado = productoRepository.save(producto);
            return ResponseEntity.ok().body(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Eliminar un producto (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        return productoRepository.findById(id).map(producto -> {
            productoRepository.delete(producto);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}