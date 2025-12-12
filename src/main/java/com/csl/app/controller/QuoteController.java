package com.csl.app.controller;

import com.csl.app.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
public class QuoteController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> enviarPresupuesto(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String transporte = body.get("transporte");
        String peso = body.get("peso");
        String precio = body.get("precio");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("El email es obligatorio.");
        }

        try {
            emailService.enviarPresupuesto(email, transporte, peso, precio);
            return ResponseEntity.ok("Presupuesto enviado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al enviar el correo.");
        }
    }
}