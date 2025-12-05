package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
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
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;


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
                .number(5000 + number + (int)(Math.random() * 1000))
                .capacity(100)
                .build());
    }

    private Screening createScreening() {
        Movie movie = createMovie();
        Hall hall = createHall(1);
        return screeningRepository.save(Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(1))
                .roomNumber(1)
                .capacity(100)
                .build());
    }

    @Test
    void testCreateMultipleSeats() throws Exception {
        Screening screening = createScreening();

        // Creăm 5 locuri diferite
        String[] rows = {"A", "A", "B", "B", "C"};
        int[] numbers = {1, 2, 3, 4, 5};
        boolean[] availability = {true, true, false, true, false};

        for (int i = 0; i < 5; i++) {
            SeatDTO dto = new SeatDTO();
            dto.setScreeningId(screening.getId());
            dto.setRow(rows[i]);
            dto.setNumber(numbers[i]);
            dto.setIsAvailable(availability[i]);

            mockMvc.perform(post("/api/seats")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.row").value(rows[i]))
                    .andExpect(jsonPath("$.number").value(numbers[i]))
                    .andExpect(jsonPath("$.is_available").value(availability[i]));
        }

        // Verificăm persistența
        assertThat(seatRepository.count()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void testGetSeatsByScreening() throws Exception {
        Screening screening = createScreening();

        // Creăm 3 locuri pentru același screening
        Seat s1 = Seat.builder()
                .screening(screening)
                .row("A")
                .number(1)
                .isAvailable(true)
                .build();
        seatRepository.save(s1);

        Seat s2 = Seat.builder()
                .screening(screening)
                .row("A")
                .number(2)
                .isAvailable(false)
                .build();
        seatRepository.save(s2);

        Seat s3 = Seat.builder()
                .screening(screening)
                .row("B")
                .number(1)
                .isAvailable(true)
                .build();
        seatRepository.save(s3);

        // Act & Assert
        mockMvc.perform(get("/api/seats/screening/{screeningId}", screening.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testGetAllSeats() throws Exception {
        Screening screening = createScreening();

        // Creăm 3 locuri
        Seat s1 = Seat.builder()
                .screening(screening)
                .row("A")
                .number(1)
                .isAvailable(true)
                .build();
        seatRepository.save(s1);

        Seat s2 = Seat.builder()
                .screening(screening)
                .row("A")
                .number(2)
                .isAvailable(false)
                .build();
        seatRepository.save(s2);

        Seat s3 = Seat.builder()
                .screening(screening)
                .row("B")
                .number(1)
                .isAvailable(true)
                .build();
        seatRepository.save(s3);

        // Act & Assert
        mockMvc.perform(get("/api/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testGetSeatById() throws Exception {
        Screening screening = createScreening();

        Seat seat = Seat.builder()
                .screening(screening)
                .row("C")
                .number(10)
                .isAvailable(true)
                .build();
        Seat saved = seatRepository.save(seat);

        // Act & Assert
        mockMvc.perform(get("/api/seats/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row").value("C"))
                .andExpect(jsonPath("$.number").value(10))
                .andExpect(jsonPath("$.is_available").value(true));
    }

    @Test
    void testCreateSeat() throws Exception {
        Screening screening = createScreening();

        SeatDTO dto = new SeatDTO();
        dto.setScreeningId(screening.getId());
        dto.setRow("D");
        dto.setNumber(5);
        dto.setIsAvailable(true);

        // Act
        String response = mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row").value("D"))
                .andExpect(jsonPath("$.number").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert - verificăm persistența
        SeatDTO created = objectMapper.readValue(response, SeatDTO.class);
        Seat seatInDb = seatRepository.findById(created.getId()).orElse(null);

        assertThat(seatInDb).isNotNull();
        assertThat(seatInDb.getRow()).isEqualTo("D");
        assertThat(seatInDb.getNumber()).isEqualTo(5);
        assertThat(seatInDb.getIsAvailable()).isTrue();
    }

    @Test
    void testUpdateSeat() throws Exception {
        Screening screening = createScreening();

        Seat seat = Seat.builder()
                .screening(screening)
                .row("E")
                .number(1)
                .isAvailable(true)
                .build();
        Seat saved = seatRepository.save(seat);

        SeatDTO dto = new SeatDTO();
        dto.setRow("E");
        dto.setNumber(2);
        dto.setIsAvailable(false);

        // Act
        mockMvc.perform(put("/api/seats/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.is_available").value(false));

        // Assert
        Seat updated = seatRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getNumber()).isEqualTo(2);
        assertThat(updated.getIsAvailable()).isFalse();
    }

    @Test
    void testPatchSeat() throws Exception {
        Screening screening = createScreening();

        Seat seat = Seat.builder()
                .screening(screening)
                .row("F")
                .number(1)
                .isAvailable(true)
                .build();
        Seat saved = seatRepository.save(seat);

        SeatDTO dto = new SeatDTO();
        dto.setIsAvailable(false);

        // Act
        mockMvc.perform(patch("/api/seats/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_available").value(false));

        // Assert
        Seat patched = seatRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getIsAvailable()).isFalse();
        assertThat(patched.getRow()).isEqualTo("F"); // nu s-a modificat
        assertThat(patched.getNumber()).isEqualTo(1); // nu s-a modificat
    }

    @Test
    void testDeleteSeat() throws Exception {
        Screening screening = createScreening();

        Seat seat = Seat.builder()
                .screening(screening)
                .row("G")
                .number(1)
                .isAvailable(true)
                .build();
        Seat saved = seatRepository.save(seat);

        // Act
        mockMvc.perform(delete("/api/seats/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(seatRepository.findById(saved.getId())).isEmpty();
    }
}