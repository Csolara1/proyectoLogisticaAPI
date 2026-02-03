package com.csl.app.controller;

import com.csl.app.model.LogEvent; // Importar LogEvent
import com.csl.app.model.User;
import com.csl.app.repository.LogEventRepository; // Importar Repositorio de Logs
import com.csl.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Para la fecha
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogEventRepository logRepository; // Inyectamos el sistema de logs

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }
        User savedUser = userRepository.save(user);

        // --- REGISTRAR LOG ---
        saveLog("INFO", "USUARIOS", "Nuevo usuario registrado: " + savedUser.getUserEmail());
        
        return savedUser;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Actualizamos datos básicos
            existingUser.setFullName(userDetails.getFullName());
            existingUser.setMobilePhone(userDetails.getMobilePhone());
            existingUser.setRoleId(userDetails.getRoleId());
            // El email NO se suele cambiar por seguridad, pero si quieres:
            // existingUser.setUserEmail(userDetails.getUserEmail());

            // --- CORRECCIÓN DE CONTRASEÑA ---
            // Solo si viene una contraseña nueva Y no está vacía, la encriptamos y guardamos.
            if (userDetails.getUserPassword() != null && !userDetails.getUserPassword().isEmpty()) {
                // ¡IMPORTANTE! Aquí es donde fallaba: hay que encriptar
                existingUser.setUserPassword(passwordEncoder.encode(userDetails.getUserPassword()));
            }
            // Si viene vacía o null, NO HACEMOS NADA (se mantiene la vieja contraseña de la DB)
            // --------------------------------

            userRepository.save(existingUser);
            return ResponseEntity.ok(existingUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        
        // --- REGISTRAR LOG ---
        saveLog("WARN", "USUARIOS", "Usuario eliminado (ID: " + id + ")");
        
        return ResponseEntity.ok().build();
    }

    // Método auxiliar para guardar logs fácilmente
    private void saveLog(String level, String module, String message) {
        LogEvent log = new LogEvent();
        log.setLogLevel(level);
        log.setSourceModule(module);
        log.setEventMessage(message);
        log.setEventTime(LocalDateTime.now());
        logRepository.save(log);
    }
}