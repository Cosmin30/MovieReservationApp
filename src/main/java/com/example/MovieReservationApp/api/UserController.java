package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ENTITY → DTO
    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getCreatedAt()
        );
    }

    // DTO → ENTITY
    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }

    // GET ALL
    @GetMapping
    public List<UserDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDTO(user);
    }

    // CREATE
    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        User user = toEntity(dto);
        user.setId(null);

        user = userRepository.save(user);
        return toDTO(user);
    }

    // UPDATE
    @PutMapping("/{id}")
    public UserDTO update(@PathVariable UUID id, @RequestBody UserDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setCreatedAt(dto.getCreatedAt());

        user = userRepository.save(user);
        return toDTO(user);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userRepository.deleteById(id);
    }
}
