package com.csl.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "destination_country")
    private String destinationCountry;

    @Column(name = "transport_mode")
    private String transportMode;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "is_processed")
    private Boolean isProcessed;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Método auxiliar para lógica de negocio clara
    public void setProcessed(boolean processed) {
        this.isProcessed = processed;
    }
}