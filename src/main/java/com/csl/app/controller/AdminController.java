package com.csl.app.controller;

import com.csl.app.model.Quote;
import com.csl.app.model.User;
import com.csl.app.service.QuoteService;
import com.csl.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Importante para desarrollo frontend separado
public class AdminController {

    private final QuoteService quoteService;
    private final UserService userService;

    @Autowired
    public AdminController(QuoteService quoteService, UserService userService) {
        this.quoteService = quoteService;
        this.userService = userService;
    }

    // Obtener todas las cotizaciones en JSON
    @GetMapping("/quotes")
    public ResponseEntity<List<Quote>> getAllQuotes() {
        return ResponseEntity.ok(quoteService.getAllQuotes());
    }

    // Marcar cotización como procesada
    @PutMapping("/quotes/{id}/process")
    public ResponseEntity<Quote> processQuote(@PathVariable Long id) {
        Quote quote = quoteService.getQuoteById(id);
        if (quote != null) {
            quote.setProcessed(true);
            return ResponseEntity.ok(quoteService.saveQuote(quote));
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener todos los usuarios en JSON
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}