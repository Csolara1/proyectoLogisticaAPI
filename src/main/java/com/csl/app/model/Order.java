package com.csl.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true)
    private String orderCode;

    @Column(name = "client_name")
    private String clientName;

    // --- NUEVO CAMPO: Vinculación con Usuario ---
    @Column(name = "user_id")
    private Long userId; 
    // ---------------------------------------------

    private String origin;
    private String destination;

    @Column(name = "transport_mode")
    private String transportMode;

    private String status;

    @Column(name = "creation_date")
    private LocalDate creationDate;
}