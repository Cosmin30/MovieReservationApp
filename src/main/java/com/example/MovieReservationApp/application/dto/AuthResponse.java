package com.example.MovieReservationApp.application.dto;

import com.example.MovieReservationApp.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;

}
