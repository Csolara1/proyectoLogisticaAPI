package com.csl.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true)
    private String orderCode; // IMPORTANTE: camelCase para que coincida con JS

    @Column(name = "client_name")
    private String clientName;

    private String origin;
    private String destination;

    @Column(name = "transport_mode")
    private String transportMode;

    private String status;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "user_id")
    private Long userId;

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = LocalDate.now();
        }
    }
}