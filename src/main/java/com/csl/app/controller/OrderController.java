package com.csl.app.controller;

import com.csl.app.model.LogEvent;
import com.csl.app.model.Order;
import com.csl.app.repository.LogEventRepository;
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
    private LogEventRepository logRepository; // Logs

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.save(order);
        
        // Log
        saveLog("INFO", "PEDIDOS", "Nuevo pedido creado #" + savedOrder.getId() + " - Cliente: " + savedOrder.getClientName());
        
        return savedOrder;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        
        // Log
        saveLog("WARN", "PEDIDOS", "Pedido #" + id + " eliminado del sistema.");
        
        return ResponseEntity.ok().build();
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