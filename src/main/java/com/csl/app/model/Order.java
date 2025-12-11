package com.csl.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders") // Nombre de la tabla en Postgres
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true)
    private String orderCode; // Ej: "1001A"

    @Column(name = "client_name")
    private String clientName;

    private String origin;
    private String destination;

    @Column(name = "transport_mode")
    private String transportMode; // Marítimo, Terrestre...

    private String status; // En Tránsito, Entregado...

    @Column(name = "creation_date")
    private LocalDate creationDate;
}