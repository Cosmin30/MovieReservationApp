package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.example.MovieReservationApp.config.TestSecurityConfig.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

//    @BeforeEach
//    void setUp() {
//        screeningRepository.deleteAll();
//        movieRepository.deleteAll();
//        hallRepository.deleteAll();
//    }

    private Movie createMovie() {
        return movieRepository.save(Movie.builder()
                .title("Test Movie")
                .genre("Action")
                .duration(120)
                .releaseDate(LocalDate.now())
                .build());
    }

    private Hall createHall(int number) {
        return hallRepository.save(Hall.builder()
                .name("Hall " + number)
                .number(3000 + number + (int)(Math.random() * 1000))
                .capacity(150)
                .build());
    }

    @Test
    void testCreateMultipleScreenings() throws Exception {
        // Creăm dependencies
        Movie savedMovie = createMovie();
        Hall savedHall = createHall(1);

        // Creăm 5 proiecții diferite
        int[] capacities = {100, 120, 150, 80, 200};
        int[] roomNumbers = {1, 2, 3, 4, 5};

        for (int i = 0; i < 5; i++) {
            ScreeningDTO dto = new ScreeningDTO();
            dto.setStartTime(OffsetDateTime.now().plusDays(i + 1));
            dto.setCapacity(capacities[i]);
            dto.setRoomNumber(roomNumbers[i]);

            mockMvc.perform(post("/api/screenings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.capacity").value(capacities[i]))
                    .andExpect(jsonPath("$.room_number").value(roomNumbers[i]));
        }

        // Verificăm persistența
        assertThat(screeningRepository.count()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void testGetAllScreenings() throws Exception {
        // Arrange - creăm 3 proiecții
        Movie movie = createMovie();
        Hall hall = createHall(1);

        Screening s1 = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(1))
                .capacity(100)
                .roomNumber(1)
                .build();
        screeningRepository.save(s1);

        Screening s2 = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(2))
                .capacity(150)
                .roomNumber(2)
                .build();
        screeningRepository.save(s2);

        Screening s3 = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(3))
                .capacity(200)
                .roomNumber(3)
                .build();
        screeningRepository.save(s3);

        // Act & Assert
        mockMvc.perform(get("/api/screenings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testGetScreeningById() throws Exception {
        // Arrange
        Movie movie = createMovie();
        Hall hall = createHall(5);

        Screening screening = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusHours(5))
                .capacity(180)
                .roomNumber(5)
                .build();
        Screening saved = screeningRepository.save(screening);

        // Act & Assert
        mockMvc.perform(get("/api/screenings/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(180))
                .andExpect(jsonPath("$.room_number").value(5));
    }

    @Test
    void testCreateScreening() throws Exception {
        // Arrange
        ScreeningDTO dto = new ScreeningDTO();
        dto.setStartTime(OffsetDateTime.now().plusDays(1));
        dto.setCapacity(120);
        dto.setRoomNumber(3);

        // Act
        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacity").value(120))
                .andExpect(jsonPath("$.room_number").value(3));
    }

    @Test
    void testUpdateScreening() throws Exception {
        // Arrange
        Movie movie = createMovie();
        Hall hall = createHall(1);

        Screening screening = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(1))
                .capacity(100)
                .roomNumber(1)
                .build();
        Screening saved = screeningRepository.save(screening);

        ScreeningDTO dto = new ScreeningDTO();
        dto.setStartTime(OffsetDateTime.now().plusDays(2));
        dto.setCapacity(200);
        dto.setRoomNumber(2);

        // Act
        mockMvc.perform(put("/api/screenings/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(200))
                .andExpect(jsonPath("$.room_number").value(2));

        // Assert
        Screening updated = screeningRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getCapacity()).isEqualTo(200);
        assertThat(updated.getRoomNumber()).isEqualTo(2);
    }

    @Test
    void testPatchScreening() throws Exception {
        // Arrange
        Movie movie = createMovie();
        Hall hall = createHall(1);

        Screening screening = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(1))
                .capacity(100)
                .roomNumber(1)
                .build();
        Screening saved = screeningRepository.save(screening);

        ScreeningDTO dto = new ScreeningDTO();
        dto.setCapacity(150);

        // Act
        mockMvc.perform(patch("/api/screenings/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(150));

        // Assert
        Screening patched = screeningRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getCapacity()).isEqualTo(150);
        assertThat(patched.getRoomNumber()).isEqualTo(1);
    }

    @Test
    void testDeleteScreening() throws Exception {
        // Arrange
        Movie movie = createMovie();
        Hall hall = createHall(7);

        Screening screening = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(1))
                .capacity(100)
                .roomNumber(7)
                .build();
        Screening saved = screeningRepository.save(screening);

        // Act
        mockMvc.perform(delete("/api/screenings/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(screeningRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testUpdateScreeningNotFound() throws Exception {
        // Arrange
        java.util.UUID nonExistentId = java.util.UUID.randomUUID();
        ScreeningDTO dto = new ScreeningDTO();
        dto.setCapacity(100);

        // Act & Assert
        mockMvc.perform(put("/api/screenings/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}