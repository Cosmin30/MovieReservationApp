package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        Movie m1 = new Movie();
        m1.setTitle("Movie1");
        m1.setDescription("Desc1");
        m1.setDuration(120);
        m1.setGenre("Action");
        m1.setReleaseDate(LocalDate.of(2023, 1, 1));

        Movie m2 = new Movie();
        m2.setTitle("Movie2");
        m2.setDescription("Desc2");
        m2.setDuration(90);
        m2.setGenre("Comedy");
        m2.setReleaseDate(LocalDate.of(2023, 2, 1));

        Mockito.when(movieRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Movie1"))
                .andExpect(jsonPath("$[1].genre").value("Comedy"));
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle("Avatar");
        movie.setDescription("Epic");
        movie.setDuration(180);
        movie.setGenre("Sci-Fi");
        movie.setReleaseDate(LocalDate.of(2022, 12, 1));

        Mockito.when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Avatar"))
                .andExpect(jsonPath("$.duration").value(180));
    }

    @Test
    void testCreate() throws Exception {
        MovieDTO dto = new MovieDTO();
        dto.setTitle("Test Movie");
        dto.setDescription("Test Desc");
        dto.setGenre("Drama");
        dto.setDuration(110);
        dto.setReleaseDate(LocalDate.of(2024, 1, 1));

        Movie saved = new Movie();
        saved.setId(UUID.randomUUID());
        saved.setTitle(dto.getTitle());
        saved.setDescription(dto.getDescription());
        saved.setGenre(dto.getGenre());
        saved.setDuration(dto.getDuration());
        saved.setReleaseDate(dto.getReleaseDate());

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

        Movie existing = new Movie();
        existing.setId(id);
        existing.setTitle("Old");
        existing.setDescription("Old Desc");
        existing.setDuration(90);
        existing.setGenre("Horror");
        existing.setReleaseDate(LocalDate.of(2023, 1, 1));

        Movie updated = new Movie();
        updated.setId(id);
        updated.setTitle("New");
        updated.setDescription("New Desc");
        updated.setDuration(150);
        updated.setGenre("Fantasy");
        updated.setReleaseDate(LocalDate.of(2024, 5, 1));

        MovieDTO dto = new MovieDTO();
        dto.setId(id);
        dto.setTitle("New");
        dto.setDescription("New Desc");
        dto.setGenre("Fantasy");
        dto.setDuration(150);
        dto.setReleaseDate(LocalDate.of(2024, 5, 1));

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

        Movie existing = new Movie();
        existing.setId(id);
        existing.setTitle("Old Title");
        existing.setDescription("Old Desc");
        existing.setDuration(100);
        existing.setGenre("Drama");
        existing.setReleaseDate(LocalDate.of(2022, 8, 1));

        Movie patched = new Movie();
        patched.setId(id);
        patched.setTitle("Patched Title");
        patched.setDescription("Old Desc");
        patched.setDuration(100);
        patched.setGenre("Drama");
        patched.setReleaseDate(LocalDate.of(2022, 8, 1));

        MovieDTO dto = new MovieDTO();
        dto.setTitle("Patched Title");

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
