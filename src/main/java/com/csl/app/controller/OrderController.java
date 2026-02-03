package com.csl.app.controller;

import com.csl.app.model.LogEvent;
import com.csl.app.model.Order;
import com.csl.app.repository.LogEventRepository;
import com.csl.app.repository.OrderRepository;
import com.csl.app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository; // Inyectamos el repo directamente para filtros rápidos

    @Autowired
    private LogEventRepository logRepository;

    @GetMapping
    public List<Order> getAllOrders(@RequestParam(required = false) Long userId) {
        // SI VIENE EL PARÁMETRO userId, FILTRAMOS
        if (userId != null) {
            return orderRepository.findByUserId(userId);
        }
        // SI NO, DEVOLVEMOS TODOS (Solo Admins deberían ver esto)
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Order newOrder = orderService.save(order);
        saveLog("INFO", "PEDIDOS", "Nuevo pedido creado: " + newOrder.getOrderCode());
        return newOrder;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return orderService.findById(id).map(existingOrder -> {
            existingOrder.setOrderCode(orderDetails.getOrderCode());
            existingOrder.setClientName(orderDetails.getClientName());
            existingOrder.setOrigin(orderDetails.getOrigin());
            existingOrder.setDestination(orderDetails.getDestination());
            existingOrder.setStatus(orderDetails.getStatus());
            existingOrder.setTransportMode(orderDetails.getTransportMode());
            existingOrder.setUserId(orderDetails.getUserId()); // Guardamos el dueño
            
            Order updated = orderService.save(existingOrder);
            saveLog("INFO", "PEDIDOS", "Pedido actualizado: " + updated.getOrderCode());
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orderService.findById(id).isPresent()) {
            orderService.deleteById(id);
            saveLog("WARN", "PEDIDOS", "Pedido eliminado ID: " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private void saveLog(String level, String module, String message) {
        try {
            LogEvent log = new LogEvent();
            log.setLogLevel(level);
            log.setSourceModule(module);
            log.setEventMessage(message);
            log.setEventTime(LocalDateTime.now());
            logRepository.save(log);
        } catch (Exception e) {
            System.err.println("Error guardando log: " + e.getMessage());
        }
    }
}