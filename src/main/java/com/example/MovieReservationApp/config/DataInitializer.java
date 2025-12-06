package com.example.MovieReservationApp.config;

import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.user.Role;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, MovieRepository movieRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@example.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .fullName("Administrator")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("Admin123!"))
                    .role(Role.ADMIN)
                    .createdAt(OffsetDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println("Admin creat: " + adminEmail);
        }
        
        // Ensure all existing users have a createdAt date
        List<User> usersWithoutDate = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() == null)
                .collect(java.util.stream.Collectors.toList());
        
        if (!usersWithoutDate.isEmpty()) {
            System.out.println("🔧 Setting createdAt for " + usersWithoutDate.size() + " users without date...");
            for (User user : usersWithoutDate) {
                user.setCreatedAt(OffsetDateTime.now().minusYears(1)); // Set to 1 year ago as default
                userRepository.save(user);
            }
            System.out.println("✅ All users now have createdAt date");
        }
        
        // Ensure all existing movies have a releaseDate
        List<Movie> moviesWithoutDate = movieRepository.findAll().stream()
                .filter(m -> m.getReleaseDate() == null)
                .collect(java.util.stream.Collectors.toList());
        
        if (!moviesWithoutDate.isEmpty()) {
            System.out.println("🔧 Setting releaseDate for " + moviesWithoutDate.size() + " movies without date...");
            for (Movie movie : moviesWithoutDate) {
                movie.setReleaseDate(LocalDate.now().minusYears(1)); // Set to 1 year ago as default
                movieRepository.save(movie);
            }
            System.out.println("✅ All movies now have releaseDate");
        }
    }
}
