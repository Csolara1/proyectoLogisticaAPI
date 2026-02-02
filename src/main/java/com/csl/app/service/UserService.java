package com.csl.app.service;

import com.csl.app.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    
    // Usamos Optional para evitar errores nulos
    Optional<User> getUserById(Long id);
    
    User saveUser(User user);
    
    void deleteUser(Long id);
    
    // IMPORTANTE: El nombre debe coincidir con la implementación
    Optional<User> findByUserEmail(String email);
}