package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.AuthRequest;
import com.example.MovieReservationApp.application.dto.AuthResponse;
import com.example.MovieReservationApp.application.dto.RegisterRequest;
import com.example.MovieReservationApp.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

}
