package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- GET ALL ----------
    @Test
    void testGetAll() throws Exception {
        User u1 = new User(UUID.randomUUID(), "John Doe", "john@mail.com", "hash1", OffsetDateTime.now(), null);
        User u2 = new User(UUID.randomUUID(), "Alice Smith", "alice@mail.com", "hash2", OffsetDateTime.now(), null);

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[1].email").value("alice@mail.com"));
    }

    // ---------- GET BY ID ----------
    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        User user = new User(id, "Mark Lee", "mark@mail.com", "hashpass", OffsetDateTime.now(), null);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Mark Lee"))
                .andExpect(jsonPath("$.email").value("mark@mail.com"));
    }

    // ---------- CREATE ----------
    @Test
    void testCreate() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setFullName("New User");
        dto.setEmail("new@mail.com");
        dto.setCreatedAt(OffsetDateTime.now());

        User saved = new User(UUID.randomUUID(), "New User", "new@mail.com", null, dto.getCreatedAt(), null);

        Mockito.when(userRepository.save(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New User"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    // ---------- UPDATE ----------
    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();

        User existing = new User(id, "Old Name", "old@mail.com", "hash", OffsetDateTime.now(), null);

        User updated = new User(id, "Updated Name", "updated@mail.com", null, OffsetDateTime.now(), null);

        UserDTO dto = new UserDTO();
        dto.setFullName("Updated Name");
        dto.setEmail("updated@mail.com");
        dto.setCreatedAt(updated.getCreatedAt());

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));
    }

    // ---------- PATCH ----------
    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        User existing = new User(id, "Patch Name", "patch@mail.com", "hashpass", OffsetDateTime.now(), null);

        User patched = new User(id, "PATCHED NEW NAME", "patch@mail.com", null, existing.getCreatedAt(), null);

        UserDTO dto = new UserDTO();
        dto.setFullName("PATCHED NEW NAME");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("PATCHED NEW NAME"));
    }

    // ---------- DELETE SUCCESS ----------
    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(userRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(userRepository).deleteById(id);
    }

    // ---------- DELETE NOT FOUND ----------
    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(userRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().is4xxClientError());
    }
}
