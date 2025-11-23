package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public UserDTO createUser(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPasswordHash("hashed_password");
        user.setCreatedAt(OffsetDateTime.now());
        userRepository.save(user);

        UserDTO result = new UserDTO();
        result.setId(user.getId());
        result.setEmail(user.getEmail());
        result.setFullName(user.getFullName());
        result.setCreatedAt(user.getCreatedAt());
        return result;
    }
}
