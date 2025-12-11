package com.csl.app.controller;

import com.csl.app.model.LogEvent;
import com.csl.app.repository.LogEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogEventController {

    @Autowired
    private LogEventRepository logRepository;

    // Obtener los últimos 20 eventos
    @GetMapping
    public List<LogEvent> getRecentLogs() {
        // Lo ideal sería ordenar por fecha desc, pero por ahora devolvemos todos
        return logRepository.findAll();
    }

    // Método para crear logs manualmente desde el frontend (opcional)
    @PostMapping
    public void createLog(@RequestBody String mensaje) {
        LogEvent log = new LogEvent();
        log.setEventMessage(mensaje);
        log.setEventTime(LocalDateTime.now());
        log.setLogLevel("INFO");
        log.setSourceModule("FRONTEND");
        logRepository.save(log);
    }

    @DeleteMapping("/logs")
public ResponseEntity<String> vaciarLogs() {
    // Asumiendo que usas un repositorio llamado logRepository
    logRepository.deleteAll(); 
    return ResponseEntity.ok("Registro de eventos vaciado correctamente.");
}
}