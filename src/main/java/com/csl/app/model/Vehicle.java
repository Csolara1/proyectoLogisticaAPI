package com.csl.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String licensePlate; // Matrícula (Ej: 8945 LMN)

    private String model; // Ej: Volvo FH16

    @Column(name = "capacity_tn")
    private Double capacityTn; // Capacidad en Toneladas

    @Column(name = "driver_name")
    private String driverName; // Conductor asignado

    private String status; // En Ruta, En Almacén...
}