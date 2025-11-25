package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.application.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUserById() throws Exception {
        UUID id = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setEmail("test@example.com");
        dto.setFullName("Test User");
        dto.setCreatedAt(OffsetDateTime.now());

        Mockito.when(userService.getUserById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("new@example.com");
        dto.setFullName("New User");

        UserDTO saved = new UserDTO();
        saved.setId(UUID.randomUUID());
        saved.setEmail(dto.getEmail());
        saved.setFullName(dto.getFullName());
        saved.setCreatedAt(OffsetDateTime.now());

        Mockito.when(userService.createUser(any(UserDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setFullName("Updated Name");

        UserDTO updated = new UserDTO();
        updated.setId(id);
        updated.setFullName("Updated Name");

        Mockito.when(userService.updateUser(eq(id), any(UserDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(userService.updateUser(eq(nonExistent), any(UserDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(put("/api/users/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setFullName("Patched Name");

        UserDTO patched = new UserDTO();
        patched.setId(id);
        patched.setFullName("Patched Name");

        Mockito.when(userService.patchUser(eq(id), any(UserDTO.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Patched Name"));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(userService.patchUser(eq(nonExistent), any(UserDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(patch("/api/users/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(id);
    }
}
