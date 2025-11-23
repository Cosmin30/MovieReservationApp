package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        Movie m1 = new Movie(UUID.randomUUID(), "Movie1", "Desc1", 120, "Action", LocalDate.of(2023,1,1), null);
        Movie m2 = new Movie(UUID.randomUUID(), "Movie2", "Desc2", 90, "Comedy", LocalDate.of(2023,2,1), null);

        Mockito.when(movieRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Movie1"))
                .andExpect(jsonPath("$[1].genre").value("Comedy"));
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();
        Movie movie = new Movie(id, "Avatar", "Epic", 180, "Sci-Fi", LocalDate.of(2022,12,1), null);

        Mockito.when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Avatar"))
                .andExpect(jsonPath("$.duration").value(180));
    }

    @Test
    void testCreate() throws Exception {
        MovieDTO dto = new MovieDTO(null, "Test Movie", "Test Desc", "Drama", 110, LocalDate.of(2024,1,1));
        Movie saved = new Movie(UUID.randomUUID(), "Test Movie", "Test Desc", 110, "Drama", LocalDate.of(2024,1,1), null);

        Mockito.when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genre").value("Drama"))
                .andExpect(jsonPath("$.duration").value(110));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();

        Movie existing = new Movie(id, "Old", "Old Desc", 90, "Horror", LocalDate.of(2023,1,1), null);
        Movie updated = new Movie(id, "New", "New Desc", 150, "Fantasy", LocalDate.of(2024,5,1), null);

        MovieDTO dto = new MovieDTO(id, "New", "New Desc", "Fantasy", 150, LocalDate.of(2024,5,1));

        Mockito.when(movieRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(movieRepository.save(any(Movie.class))).thenReturn(updated);

        mockMvc.perform(put("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.duration").value(150));
    }

    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        Movie existing = new Movie(id, "Old Title", "Old Desc", 100, "Drama", LocalDate.of(2022,8,1), null);
        Movie patched = new Movie(id, "Patched Title", "Old Desc", 100, "Drama", LocalDate.of(2022,8,1), null);

        MovieDTO dto = new MovieDTO(null, "Patched Title", null, null, null, null);

        Mockito.when(movieRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(movieRepository.save(any(Movie.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched Title"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(movieRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(movieRepository).deleteById(id);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(movieRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().is4xxClientError());
    }
}
