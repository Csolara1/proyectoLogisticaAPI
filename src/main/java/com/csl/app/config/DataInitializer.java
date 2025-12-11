package com.csl.app.config;

import com.csl.app.model.*;
import com.csl.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder,
            OrderRepository orderRepository,
            VehicleRepository vehicleRepository,
            StockItemRepository stockItemRepository) {
        
        return args -> {
            // 1. USUARIOS (Sustituye a tu tabla USERS y ROLES)
            if (userRepository.findByUserEmail("admin@csl.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Super Admin");
                admin.setUserEmail("admin@csl.com");
                admin.setMobilePhone("600123456");
                admin.setRoleId(1); // 1 = Admin, 2 = Usuario
                admin.setIsActive(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUserPassword(passwordEncoder.encode("cowabunga")); 
                userRepository.save(admin);
                System.out.println("✅ Usuario ADMIN creado.");
            }

            // 2. PEDIDOS (Sustituye y mejora tu idea de CRUD)
            if (orderRepository.count() == 0) {
                Order o1 = new Order();
                o1.setOrderCode("1001A");
                o1.setClientName("Inversiones Delta");
                o1.setOrigin("Shanghai");
                o1.setDestination("Valencia");
                o1.setTransportMode("Marítimo");
                o1.setStatus("En Tránsito");
                o1.setCreationDate(LocalDate.now());
                orderRepository.save(o1);
            }

            // 3. VEHÍCULOS (Nuevo)
            if (vehicleRepository.count() == 0) {
                Vehicle v1 = new Vehicle();
                v1.setLicensePlate("8945 LMN");
                v1.setModel("Volvo FH16");
                v1.setCapacityTn(40.0);
                v1.setDriverName("Antonio R.");
                v1.setStatus("En Ruta");
                vehicleRepository.save(v1);
            }

            // 4. STOCK (Nuevo)
            if (stockItemRepository.count() == 0) {
                StockItem s1 = new StockItem();
                s1.setProductReference("PROD-A345");
                s1.setWarehouse("MAD-01");
                s1.setLocation("Aisle 10-B");
                s1.setQuantity(1250);
                s1.setUnit("Cajas");
                s1.setLastEntryDate(LocalDate.now());
                stockItemRepository.save(s1);
            }
        };
    }
}