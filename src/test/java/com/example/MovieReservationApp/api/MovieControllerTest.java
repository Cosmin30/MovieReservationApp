package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.application.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        MovieDTO m1 = new MovieDTO();
        m1.setId(UUID.randomUUID());
        m1.setTitle("Inception");
        m1.setGenre("Sci-Fi");
        m1.setDuration(148);
        m1.setReleaseDate(LocalDate.of(2010, 7, 16));

        MovieDTO m2 = new MovieDTO();
        m2.setId(UUID.randomUUID());
        m2.setTitle("The Matrix");
        m2.setGenre("Action");
        m2.setDuration(136);
        m2.setReleaseDate(LocalDate.of(1999, 3, 31));

        Mockito.when(movieService.getAllMovies()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[1].genre").value("Action"));
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        MovieDTO movie = new MovieDTO();
        movie.setId(id);
        movie.setTitle("Interstellar");
        movie.setDescription("Space exploration");
        movie.setGenre("Sci-Fi");
        movie.setDuration(169);
        movie.setReleaseDate(LocalDate.of(2014, 11, 7));

        Mockito.when(movieService.getMovieById(id)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Interstellar"))
                .andExpect(jsonPath("$.duration").value(169));
    }

    @Test
    void testCreate() throws Exception {
        MovieDTO dto = new MovieDTO();
        dto.setTitle("New Movie");
        dto.setDescription("Test description");
        dto.setGenre("Drama");
        dto.setDuration(120);
        dto.setReleaseDate(LocalDate.of(2024, 1, 1));

        MovieDTO saved = new MovieDTO();
        saved.setId(UUID.randomUUID());
        saved.setTitle("New Movie");
        saved.setDescription("Test description");
        saved.setGenre("Drama");
        saved.setDuration(120);
        saved.setReleaseDate(LocalDate.of(2024, 1, 1));

        Mockito.when(movieService.createMovie(any(MovieDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Movie"))
                .andExpect(jsonPath("$.genre").value("Drama"));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();

        MovieDTO dto = new MovieDTO();
        dto.setId(id);
        dto.setTitle("Updated Movie");
        dto.setDescription("Updated description");
        dto.setGenre("Thriller");
        dto.setDuration(130);
        dto.setReleaseDate(LocalDate.of(2024, 6, 15));

        MovieDTO updated = new MovieDTO();
        updated.setId(id);
        updated.setTitle("Updated Movie");
        updated.setDescription("Updated description");
        updated.setGenre("Thriller");
        updated.setDuration(130);
        updated.setReleaseDate(LocalDate.of(2024, 6, 15));

        Mockito.when(movieService.updateMovie(eq(id), any(MovieDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Movie"))
                .andExpect(jsonPath("$.genre").value("Thriller"));
    }

    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        MovieDTO dto = new MovieDTO();
        dto.setTitle("Patched Title");
        dto.setDuration(140);

        MovieDTO modified = new MovieDTO();
        modified.setId(id);
        modified.setTitle("Patched Title");
        modified.setDescription("Original description");
        modified.setGenre("Action");
        modified.setDuration(140);
        modified.setReleaseDate(LocalDate.of(2020, 5, 10));

        Mockito.when(movieService.patchMovie(eq(id), any(MovieDTO.class))).thenReturn(modified);

        mockMvc.perform(patch("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched Title"))
                .andExpect(jsonPath("$.duration").value(140));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(movieService).deleteMovie(id);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(movieService).deleteMovie(id);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Movie not found"))
                .when(movieService).deleteMovie(id);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isNotFound());
    }
}