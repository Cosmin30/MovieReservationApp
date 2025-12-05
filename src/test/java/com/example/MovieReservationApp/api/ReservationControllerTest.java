package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.example.MovieReservationApp.config.TestSecurityConfig.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

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
//        reservationRepository.deleteAll();
//    }

    private User createUser(String suffix) {
        return userRepository.save(User.builder()
                .fullName("Test User " + suffix)
                .email("user" + suffix + System.currentTimeMillis() + "@test.com")
                .passwordHash("password123")
                .createdAt(OffsetDateTime.now())
                .build());
    }

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
                .number(2000 + number + (int)(Math.random() * 1000))
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
    void testCreateMultipleReservations() throws Exception {
        // Creăm dependencies
        User savedUser = createUser("multi");
        Screening savedScreening = createScreening();

        // Creăm 5 rezervări
        BigDecimal[] prices = {
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(75),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(150)
        };

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/reservations")
                            .param("userId", savedUser.getId().toString())
                            .param("screeningId", savedScreening.getId().toString())
                            .param("seatIds", java.util.UUID.randomUUID().toString())
                            .param("pricePerSeat", prices[i].toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }

        // Verificăm persistența
        assertThat(reservationRepository.count()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void testGetAllReservations() throws Exception {
        // Arrange
        User user = createUser("all");
        Screening screening = createScreening();

        Reservation r1 = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(100))
                .createdAt(OffsetDateTime.now())
                .build();
        reservationRepository.save(r1);

        Reservation r2 = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("PAID")
                .totalPrice(BigDecimal.valueOf(150))
                .createdAt(OffsetDateTime.now())
                .build();
        reservationRepository.save(r2);

        Reservation r3 = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CONFIRMED")
                .totalPrice(BigDecimal.valueOf(200))
                .createdAt(OffsetDateTime.now())
                .build();
        reservationRepository.save(r3);

        // Act & Assert
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testGetReservationById() throws Exception {
        // Arrange
        User user = createUser("byid");
        Screening screening = createScreening();

        Reservation reservation = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("PAID")
                .totalPrice(BigDecimal.valueOf(125))
                .createdAt(OffsetDateTime.now())
                .build();
        Reservation saved = reservationRepository.save(reservation);

        // Act & Assert
        mockMvc.perform(get("/api/reservations/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.total_price").value(125));
    }

    @Test
    void testGetReservationsByUser() throws Exception {
        // Arrange
        User savedUser = createUser("byuser");
        Screening screening = createScreening();

        Reservation r1 = Reservation.builder()
                .user(savedUser)
                .screening(screening)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(50))
                .createdAt(OffsetDateTime.now())
                .build();
        reservationRepository.save(r1);

        Reservation r2 = Reservation.builder()
                .user(savedUser)
                .screening(screening)
                .status("PAID")
                .totalPrice(BigDecimal.valueOf(75))
                .createdAt(OffsetDateTime.now())
                .build();
        reservationRepository.save(r2);

        // Act & Assert
        mockMvc.perform(get("/api/reservations/user/{userId}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void testUpdateReservation() throws Exception {
        // Arrange
        User user = createUser("update");
        Screening screening = createScreening();

        Reservation reservation = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(100))
                .createdAt(OffsetDateTime.now())
                .build();
        Reservation saved = reservationRepository.save(reservation);

        String requestJson = """
            {
                "status": "PAID",
                "total_price": 150
            }
            """;

        // Act
        mockMvc.perform(put("/api/reservations/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.total_price").value(150));

        // Assert
        Reservation updated = reservationRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("PAID");
        assertThat(updated.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void testPatchReservation() throws Exception {
        // Arrange
        User user = createUser("patch");
        Screening screening = createScreening();

        Reservation reservation = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(100))
                .createdAt(OffsetDateTime.now())
                .build();
        Reservation saved = reservationRepository.save(reservation);

        String requestJson = """
            {
                "status": "CANCELLED"
            }
            """;

        // Act
        mockMvc.perform(patch("/api/reservations/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Assert
        Reservation patched = reservationRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getStatus()).isEqualTo("CANCELLED");
        assertThat(patched.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void testDeleteReservation() throws Exception {
        // Arrange
        User user = createUser("delete");
        Screening screening = createScreening();

        Reservation reservation = Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CANCELLED")
                .totalPrice(BigDecimal.valueOf(50))
                .createdAt(OffsetDateTime.now())
                .build();
        Reservation saved = reservationRepository.save(reservation);

        // Act
        mockMvc.perform(delete("/api/reservations/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(reservationRepository.findById(saved.getId())).isEmpty();
    }
}