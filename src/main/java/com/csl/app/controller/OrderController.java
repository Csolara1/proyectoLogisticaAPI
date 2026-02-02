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
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LogEventRepository logRepository;

    // --- GET CON FILTRO DE USUARIO ---
    @GetMapping
    public List<Order> getAllOrders(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            // Si es Cliente, usamos el Repositorio directo para filtrar
            return orderRepository.findByUserId(userId);
        }
        // Si es Admin, usamos el Servicio para traer todo (CORREGIDO: findAll)
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        // CORREGIDO: findById
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // CORREGIDO: save
        Order newOrder = orderService.save(order);
        saveLog("INFO", "PEDIDOS", "Nuevo pedido creado: " + newOrder.getOrderCode());
        return newOrder;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        // CORREGIDO: findById
        return orderService.findById(id).map(existingOrder -> {
            existingOrder.setClientName(orderDetails.getClientName());
            existingOrder.setOrigin(orderDetails.getOrigin());
            existingOrder.setDestination(orderDetails.getDestination());
            existingOrder.setStatus(orderDetails.getStatus());
            existingOrder.setTransportMode(orderDetails.getTransportMode());
            existingOrder.setUserId(orderDetails.getUserId()); 
            
            // CORREGIDO: save
            Order updated = orderService.save(existingOrder);
            saveLog("INFO", "PEDIDOS", "Pedido actualizado: " + updated.getOrderCode());
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        // CORREGIDO: findById y deleteById
        if (orderService.findById(id).isPresent()) {
            orderService.deleteById(id);
            saveLog("WARN", "PEDIDOS", "Pedido eliminado ID: " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private void saveLog(String level, String module, String message) {
        LogEvent log = new LogEvent();
        log.setLogLevel(level);
        log.setSourceModule(module);
        log.setEventMessage(message);
        log.setEventTime(LocalDateTime.now());
        logRepository.save(log);
    }
}