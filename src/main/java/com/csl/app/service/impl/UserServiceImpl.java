package com.csl.app.service.impl;

import com.csl.app.model.User;
import com.csl.app.repository.OrderRepository;
import com.csl.app.repository.QuoteRepository;
import com.csl.app.repository.UserRepository;
import com.csl.app.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private OrderRepository orderRepository; 

    @Autowired(required = false)
    private QuoteRepository quoteRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            // 1. Borramos datos hijos usando el ID del usuario
            try {
                // CORREGIDO: Usamos deleteByUserId
                if (orderRepository != null) orderRepository.deleteByUserId(id);
                if (quoteRepository != null) quoteRepository.deleteByUserId(id);
            } catch (Exception e) {
                System.out.println("Aviso al borrar datos cascada: " + e.getMessage());
            }

            // 2. Borramos al usuario
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    @Override
    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }
}