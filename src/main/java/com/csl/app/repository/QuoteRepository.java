package com.csl.app.repository;

import com.csl.app.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    // CORREGIDO: Buscamos por ID (userId) en lugar de por objeto (user)
    List<Quote> findByUserId(Long userId);
    
    // Para borrar en cascada
    void deleteByUserId(Long userId);

    List<Quote> findByIsProcessedFalse();
}