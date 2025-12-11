package com.csl.app.controller;

import com.csl.app.model.User;
import com.csl.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // LOGIN REAL
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("userEmail");
        String password = credentials.get("userPassword");

        Optional<User> userOpt = userRepository.findByUserEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verificamos si la contraseña coincide (la cruda vs la encriptada)
            if (passwordEncoder.matches(password, user.getUserPassword())) {
                // Login exitoso: Devolvemos los datos del usuario (sin la pass)
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

    // REGISTRO PÚBLICO
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado");
        }

        // Configuración por defecto para nuevos registros
        user.setIsActive(true);
        user.setRoleId(2); // 2 = CLIENTE por defecto (así no se registran como admins)
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        User newUser = userRepository.save(user);
        return ResponseEntity.ok(newUser);
    }
}