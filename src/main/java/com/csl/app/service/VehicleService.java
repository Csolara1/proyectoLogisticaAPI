package com.csl.app.service;

import com.csl.app.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> findAll();
    Optional<Vehicle> findById(Long id);
    Vehicle save(Vehicle vehicle);
    void deleteById(Long id);
}