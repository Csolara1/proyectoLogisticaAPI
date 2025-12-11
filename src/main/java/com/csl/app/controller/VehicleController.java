package com.csl.app.controller;

import com.csl.app.model.LogEvent;
import com.csl.app.model.Vehicle;
import com.csl.app.repository.LogEventRepository;
import com.csl.app.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LogEventRepository logRepository; // Logs

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.findAll();
    }

    @PostMapping
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.save(vehicle);
        
        // Log
        saveLog("INFO", "FLOTA", "Nuevo vehículo registrado: " + savedVehicle.getLicensePlate());
        
        return savedVehicle;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteById(id);
        
        // Log
        saveLog("WARN", "FLOTA", "Vehículo dado de baja (ID: " + id + ")");
        
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