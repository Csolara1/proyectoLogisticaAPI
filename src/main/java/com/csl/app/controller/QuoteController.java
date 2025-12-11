package com.csl.app.controller;

import com.csl.app.model.Quote;
import com.csl.app.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // <--- Cambiamos a RestController
@RequestMapping("/api/quotes") // Prefijo para la API
@CrossOrigin(origins = "*") // Permite que tu Frontend externo se conecte sin bloquearse
public class QuoteController {

    private final QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    // Endpoint para guardar un presupuesto (Lo llamará tu Frontend)
    @PostMapping
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote) {
        Quote savedQuote = quoteService.saveQuote(quote);
        return ResponseEntity.ok(savedQuote); // Devuelve el objeto guardado en JSON
    }
}