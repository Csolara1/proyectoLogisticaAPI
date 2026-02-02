package com.csl.app.repository;

import com.csl.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Método para buscar pedidos de un solo usuario (Vista Cliente)
    List<Order> findByUserId(Long userId);

    // Método para borrar pedidos de un usuario (Borrado en cascada)
    void deleteByUserId(Long userId);
}