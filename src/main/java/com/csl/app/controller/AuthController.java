package com.csl.app.controller;

import com.csl.app.model.User;
import com.csl.app.repository.UserRepository;
import com.csl.app.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Almacenes temporales de tokens
    private static final Map<String, String> passwordResetTokens = new ConcurrentHashMap<>();
    private static final Map<String, String> registrationTokens = new ConcurrentHashMap<>();

    // --- LOGIN (Bloquea usuarios no activados) ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("userEmail");
        String password = credentials.get("userPassword");

        Optional<User> userOpt = userRepository.findByUserEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // VERIFICACIÓN DE CUENTA ACTIVA
            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tu cuenta no está activa. Por favor, revisa tu correo y confirma el registro.");
            }

            if (passwordEncoder.matches(password, user.getUserPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login exitoso");
                response.put("userId", user.getUserId());
                response.put("fullName", user.getFullName());
                response.put("roleId", user.getRoleId());
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    // --- REGISTRO (Crea inactivo + Envía correo) ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado");
        }

        // 1. Configuración inicial (INACTIVO)
        user.setIsActive(false); 
        user.setRoleId(2); // Cliente
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        
        User newUser = userRepository.save(user);

        // 2. Generar Token y Enviar Correo
        String token = UUID.randomUUID().toString();
        registrationTokens.put(token, newUser.getUserEmail());

        try {
            emailService.enviarCorreoRegistro(newUser.getUserEmail(), token);
            return ResponseEntity.ok("Registro completado. Se ha enviado un correo de confirmación.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Usuario creado pero falló el envío del correo.");
        }
    }

    // --- CONFIRMAR CUENTA (Endpoint del enlace del correo) ---
    @GetMapping("/confirm-account")
    public void confirmAccount(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        String email = registrationTokens.get(token);

        if (email == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "El enlace de confirmación es inválido o ha caducado.");
            return;
        }

        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true); // ¡ACTIVAMOS AL USUARIO!
            userRepository.save(user);
            registrationTokens.remove(token);

            // Redirigir al Login del Frontend (Ajusta el puerto 5500 si es necesario)
            response.sendRedirect("http://127.0.0.1:5500/login.html?verified=true");
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.");
        }
    }

    // --- SOLICITAR RECUPERACIÓN (Forgot Password) ---
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isEmpty()) return ResponseEntity.badRequest().body("El email es obligatorio.");

        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Usuario no encontrado.");

        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, email);

        try {
            emailService.enviarCorreoRecuperacion(email, token);
            return ResponseEntity.ok("Correo enviado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error envío correo: " + e.getMessage());
        }
    }

    // --- RESTABLECER CONTRASEÑA (Reset Password) ---
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null) return ResponseEntity.badRequest().body("Faltan datos.");

        String email = passwordResetTokens.get(token);
        if (email == null) return ResponseEntity.badRequest().body("Token inválido.");

        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUserPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            passwordResetTokens.remove(token);
            return ResponseEntity.ok("¡Contraseña actualizada!");
        }
        return ResponseEntity.badRequest().body("Usuario no encontrado.");
    }
}