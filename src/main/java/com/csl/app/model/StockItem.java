package com.csl.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stock_items")
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_reference", unique = true)
    private String productReference; // Ej: PROD-A345

    private String warehouse; // Ej: MAD-01

    private String location; // Ej: Aisle 10-B

    private Integer quantity;

    private String unit; // Cajas, Pallets...

    @Column(name = "last_entry_date")
    private LocalDate lastEntryDate;
}