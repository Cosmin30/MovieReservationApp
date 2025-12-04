package com.example.MovieReservationApp.config;

import com.example.MovieReservationApp.domain.model.user.Role;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@example.com";

        // Verificăm dacă există deja un admin
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .fullName("Administrator")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("Admin123!")) // parola default
                    .role(Role.ADMIN)
                    .createdAt(OffsetDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println("✅ Admin creat: " + adminEmail);
        }
    }
}
