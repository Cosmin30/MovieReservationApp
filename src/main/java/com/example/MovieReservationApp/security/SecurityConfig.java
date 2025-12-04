package com.example.MovieReservationApp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // GET requests - toate pentru utilizatori autentificați
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/screenings/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/seats/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/halls/**").authenticated()
                        // POST, PUT, DELETE, PATCH - doar pentru admin
                        .requestMatchers(HttpMethod.POST, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/screenings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/screenings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/screenings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/screenings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/seats/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/seats/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/seats/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/seats/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/halls/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/halls/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/halls/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/halls/**").hasRole("ADMIN")
                        // Users endpoint
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // Reservations - POST pentru utilizatori autentificați, rest pentru admin
                        .requestMatchers(HttpMethod.POST, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").hasRole("ADMIN")
                        // Alte request-uri necesită autentificare
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Admin-Secret"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
