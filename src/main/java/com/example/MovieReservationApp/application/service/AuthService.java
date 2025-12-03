package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.AuthRequest;
import com.example.MovieReservationApp.application.dto.AuthResponse;
import com.example.MovieReservationApp.application.dto.RegisterRequest;
import com.example.MovieReservationApp.domain.model.user.Role;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import com.example.MovieReservationApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid login"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid login");

        return new AuthResponse(jwtService.generateToken(user), user.getRole());
    }

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);

        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user), user.getRole());
    }
}
