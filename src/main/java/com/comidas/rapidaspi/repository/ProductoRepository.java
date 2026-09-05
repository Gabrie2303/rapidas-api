package com.comidas.rapidaspi.repository;

import com.comidas.rapidaspi.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Método personalizado para consultar solo los productos disponibles en el menú
    List<Producto> findByDisponibleTrue();
}