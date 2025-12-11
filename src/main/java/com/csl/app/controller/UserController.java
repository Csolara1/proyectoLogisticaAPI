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
@CrossOrigin(origins = "*")
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
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setFullName(userDetails.getFullName());
            existingUser.setUserEmail(userDetails.getUserEmail());
            existingUser.setRoleId(userDetails.getRoleId());
            existingUser.setMobilePhone(userDetails.getMobilePhone());
            existingUser.setIsActive(userDetails.getIsActive());
            
            if (userDetails.getUserPassword() != null && !userDetails.getUserPassword().isEmpty()) {
                existingUser.setUserPassword(passwordEncoder.encode(userDetails.getUserPassword()));
            }
            
            User updatedUser = userRepository.save(existingUser);

            // --- REGISTRAR LOG ---
            saveLog("INFO", "USUARIOS", "Usuario actualizado: " + updatedUser.getUserEmail());

            return ResponseEntity.ok(updatedUser);
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