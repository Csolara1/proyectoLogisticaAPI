package com.csl.app.repository;

import com.csl.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Permite encontrar pedidos de un usuario específico
    List<Order> findByUserId(Long userId);

    // Permite borrar todos los pedidos de un usuario (para el borrado en cascada)
    void deleteByUserId(Long userId);
}