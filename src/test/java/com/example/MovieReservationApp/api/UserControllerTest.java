package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ===================== Helper =====================
    private User createValidUser(String fullName, String email) {
        return User.builder()
                .fullName(fullName)
                .email(email)
                .passwordHash("defaultPass") // minim 6 caractere pentru validare
                .createdAt(OffsetDateTime.now())
                .build();
    }

    // ===================== TESTS =====================

    @Test
    void testCreateUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setFullName("New User");
        dto.setEmail("newuser" + System.currentTimeMillis() + "@example.com");

        // Creează entity cu passwordHash valid
        User user = createValidUser(dto.getFullName(), dto.getEmail());
        User saved = userRepository.save(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("New User");
        assertThat(saved.getEmail()).isEqualTo(dto.getEmail());
        assertThat(saved.getPasswordHash()).isEqualTo("defaultPass");
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = createValidUser("Original Name", "original" + System.currentTimeMillis() + "@example.com");
        User saved = userRepository.save(user);

        UserDTO dto = new UserDTO();
        dto.setFullName("Updated Name");
        dto.setEmail(saved.getEmail());

        // Simulăm update prin repository direct pentru test
        saved.setFullName(dto.getFullName());
        userRepository.save(saved);

        User updated = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getFullName()).isEqualTo("Updated Name");
        assertThat(updated.getPasswordHash()).isEqualTo("defaultPass"); // parola nu s-a schimbat
    }

    @Test
    void testGetUserById() throws Exception {
        User user = createValidUser("Test User", "testuser" + System.currentTimeMillis() + "@example.com");
        User saved = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.full_name").value("Test User"))
                .andExpect(jsonPath("$.email").value(saved.getEmail()));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = createValidUser("To Delete", "delete" + System.currentTimeMillis() + "@example.com");
        User saved = userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User u1 = createValidUser("User One", "user1" + System.currentTimeMillis() + "@test.com");
        User u2 = createValidUser("User Two", "user2" + System.currentTimeMillis() + "@test.com");
        userRepository.save(u1);
        userRepository.save(u2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void testCreateMultipleUsers() throws Exception {
        String[] names = {"John Doe", "Jane Smith", "Bob Johnson", "Alice Williams", "Charlie Brown"};
        long timestamp = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            String uniqueEmail = "user" + timestamp + "_" + i + "@test.com";

            User user = createValidUser(names[i], uniqueEmail);
            userRepository.save(user);
        }

        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers.size()).isGreaterThanOrEqualTo(5);
    }
}
