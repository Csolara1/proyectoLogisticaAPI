package com.csl.app.repository;

import com.csl.app.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    // Encuentra todas las cotizaciones que NO han sido procesadas
    List<Quote> findByIsProcessedFalse();
}