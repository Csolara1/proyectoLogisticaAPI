package com.csl.app.repository;

import com.csl.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Método mágico de Spring Data JPA: busca por campo userEmail
    Optional<User> findByUserEmail(String userEmail);
}