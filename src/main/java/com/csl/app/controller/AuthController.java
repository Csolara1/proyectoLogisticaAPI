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
import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Almacenes de tokens
    private static final Map<String, String> passwordResetTokens = new ConcurrentHashMap<>();
    private static final Map<String, String> registrationTokens = new ConcurrentHashMap<>();

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("userEmail");
        String password = credentials.get("userPassword");

        Optional<User> userOpt = userRepository.findByUserEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!Boolean.TRUE.equals(user.getIsActive())) { // Null-safe check
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Tu cuenta no está activa. Revisa tu correo.");
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

    // --- REGISTRO MANUAL ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado");
        }
        user.setIsActive(false);
        user.setRoleId(2);
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        User newUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        registrationTokens.put(token, newUser.getUserEmail());

        try {
            emailService.enviarCorreoRegistro(newUser.getUserEmail(), token);
            return ResponseEntity.ok("Registro completado. Revisa tu correo.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo envío correo.");
        }
    }

    // --- CONFIRMAR CUENTA MANUAL ---
    @GetMapping("/confirm-account")
    public void confirmAccount(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        String email = registrationTokens.get(token);
        if (email == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Enlace inválido.");
            return;
        }
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            userRepository.save(user);
            registrationTokens.remove(token);
            response.sendRedirect("https://controlsystemlogistic.com/login.html?verified=true");
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.");
        }
    }

    // --- FORGOT PASSWORD ---
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) return ResponseEntity.badRequest().body("Email obligatorio.");

        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");

        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, email);

        try {
            emailService.enviarCorreoRecuperacion(email, token);
            return ResponseEntity.ok("Correo enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error envío correo.");
        }
    }

    // --- RESET PASSWORD (INTACTO - Solo cambia contraseña) ---
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

    // --- NUEVO ENDPOINT: COMPLETAR PERFIL (Solo para Google/Nuevos) ---
    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        String fullName = body.get("fullName");
        String phone = body.get("mobilePhone");

        if (token == null || newPassword == null)
            return ResponseEntity.badRequest().body("Faltan datos obligatorios.");

        String email = passwordResetTokens.get(token);
        if (email == null) 
            return ResponseEntity.badRequest().body("Token inválido o expirado.");

        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Actualizamos datos
            user.setUserPassword(passwordEncoder.encode(newPassword));
            if (fullName != null && !fullName.isEmpty()) user.setFullName(fullName);
            if (phone != null && !phone.isEmpty()) user.setMobilePhone(phone);
            
            // ¡ESTO ES LO IMPORTANTE! ACTIVAMOS LA CUENTA
            user.setIsActive(true);
            
            userRepository.save(user);
            passwordResetTokens.remove(token);
            
            return ResponseEntity.ok("¡Perfil completado y cuenta activada!");
        }
        return ResponseEntity.badRequest().body("Usuario no encontrado.");
    }

    // --- LOGIN CON GOOGLE (MODIFICADO) ---
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            
            // Decodificar token (Simplificado)
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);
            
            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            
            if (email == null) return ResponseEntity.badRequest().body("Token inválido");

            Optional<User> userOpt = userRepository.findByUserEmail(email);

            if (userOpt.isPresent()) {
                // CASO A: YA EXISTE -> Login Normal
                User user = userOpt.get();
                
                if (!Boolean.TRUE.equals(user.getIsActive())) {
                     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cuenta inactiva. Revisa tu correo.");
                }

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login Google exitoso");
                response.put("userId", user.getUserId());
                response.put("fullName", user.getFullName());
                response.put("roleId", user.getRoleId());
                response.put("userEmail", user.getUserEmail());
                return ResponseEntity.ok(response);

            } else {
                // CASO B: NUEVO -> Crear Inactivo y Enviar Correo Configuración
                User newUser = new User();
                newUser.setUserEmail(email);
                newUser.setFullName(name != null ? name : email);
                newUser.setRoleId(2);
                newUser.setIsActive(false); 
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setUserPassword(""); 
                
                userRepository.save(newUser);

                // Generamos token para complete_profile
                String setupToken = UUID.randomUUID().toString();
                passwordResetTokens.put(setupToken, email);

                // ENVIAMOS EL NUEVO CORREO (Ahora sí existe el método)
                emailService.enviarCorreoConfiguracion(email, setupToken);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "NEW_USER_EMAIL_SENT");
                response.put("message", "Usuario registrado. Revisa tu correo.");
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Google Login");
        }
    }
}