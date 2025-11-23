package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScreeningController.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScreeningRepository screeningRepository;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        Movie movie = new Movie(UUID.randomUUID(), "MovieA", "Desc", 120, "Action", null, null);
        Hall hall = new Hall(UUID.randomUUID(), 1, 100, "Hall A");

        Screening s1 = new Screening(UUID.randomUUID(), movie, OffsetDateTime.now(), 1, hall, 100, null);
        Screening s2 = new Screening(UUID.randomUUID(), movie, OffsetDateTime.now(), 2, hall, 200, null);

        Mockito.when(screeningRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/api/screenings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value(1))
                .andExpect(jsonPath("$[1].capacity").value(200));
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        Movie movie = new Movie(UUID.randomUUID(), "Avatar", "Epic", 180, "Sci-Fi", null, null);
        Hall hall = new Hall(UUID.randomUUID(), 2, 150, "VIP Hall");

        Screening screening = new Screening(id, movie, OffsetDateTime.now(), 5, hall, 150, null);

        Mockito.when(screeningRepository.findById(id)).thenReturn(Optional.of(screening));

        mockMvc.perform(get("/api/screenings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value(5))
                .andExpect(jsonPath("$.hall.id").value(hall.getId().toString()))
                .andExpect(jsonPath("$.movie.id").value(movie.getId().toString()));
    }

    @Test
    void testCreate() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID hallId = UUID.randomUUID();

        Movie movie = new Movie(movieId, "Test Movie", "Desc", 100, "Genre", null, null);
        Hall hall = new Hall(hallId, 1, 200, "Hall X");

        ScreeningDTO dto = new ScreeningDTO();
        dto.setMovie(new MovieDTO());
        dto.getMovie().setId(movieId);

        dto.setHall(new HallDTO());
        dto.getHall().setId(hallId);

        dto.setStartTime(OffsetDateTime.now());
        dto.setRoomNumber(3);
        dto.setCapacity(120);

        Screening saved = new Screening(UUID.randomUUID(), movie, dto.getStartTime(), 3, hall, 120, null);

        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        Mockito.when(hallRepository.findById(hallId)).thenReturn(Optional.of(hall));
        Mockito.when(screeningRepository.save(any(Screening.class))).thenReturn(saved);

        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(120))
                .andExpect(jsonPath("$.movie.id").value(movieId.toString()));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID hallId = UUID.randomUUID();

        Movie movie = new Movie(movieId, "M", "D", 100, "G", null, null);
        Hall hall = new Hall(hallId, 1, 100, "Hall Y");

        Screening existing = new Screening(id, movie, OffsetDateTime.now(), 1, hall, 100, null);

        Screening updated = new Screening(id, movie, OffsetDateTime.now(), 10, hall, 500, null);

        ScreeningDTO dto = new ScreeningDTO();
        dto.setMovie(new MovieDTO());
        dto.getMovie().setId(movieId);

        dto.setHall(new HallDTO());
        dto.getHall().setId(hallId);

        dto.setStartTime(updated.getStartTime());
        dto.setRoomNumber(10);
        dto.setCapacity(500);

        Mockito.when(screeningRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        Mockito.when(hallRepository.findById(hallId)).thenReturn(Optional.of(hall));
        Mockito.when(screeningRepository.save(any(Screening.class))).thenReturn(updated);

        mockMvc.perform(put("/api/screenings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(500))
                .andExpect(jsonPath("$.roomNumber").value(10));
    }

    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        Movie movie = new Movie(UUID.randomUUID(), "T", "D", 90, "G", null, null);
        Hall hall = new Hall(UUID.randomUUID(), 1, 300, "Hall P");

        Screening existing = new Screening(id, movie, OffsetDateTime.now(), 2, hall, 200, null);

        Screening patched = new Screening(id, movie, existing.getStartTime(), 5, hall, 200, null);

        ScreeningDTO dto = new ScreeningDTO();
        dto.setRoomNumber(5);

        Mockito.when(screeningRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(screeningRepository.save(any(Screening.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/screenings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value(5));
    }

    @Test
    void testDelete() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/screenings/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(screeningRepository).deleteById(id);
    }
}
