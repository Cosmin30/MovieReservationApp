package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
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
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

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
//        paymentRepository.deleteAll();
//        reservationRepository.deleteAll();
//        screeningRepository.deleteAll();
//        movieRepository.deleteAll();
//        hallRepository.deleteAll();
//        userRepository.deleteAll();
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
                .number(1000 + number + (int)(Math.random() * 1000))
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

    private Reservation createReservation(String suffix) {
        User user = createUser(suffix);
        Screening screening = createScreening();
        return reservationRepository.save(Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(250))
                .createdAt(OffsetDateTime.now())
                .build());
    }

    @Test
    void testCreateMultiplePayments() throws Exception {
        Reservation savedReservation = createReservation("multi");

        // Creăm 5 plăți diferite
        String[] statuses = {"PAID", "PENDING", "PAID", "FAILED", "PAID"};
        BigDecimal[] amounts = {
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(75.50),
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(25.75),
                BigDecimal.valueOf(150.00)
        };

        for (int i = 0; i < 5; i++) {
            PaymentDTO dto = new PaymentDTO();
            dto.setReservationId(savedReservation.getId());
            dto.setAmount(amounts[i]);
            dto.setStatus(statuses[i]);

            if (statuses[i].equals("PAID")) {
                dto.setPaidAt(OffsetDateTime.now());
            }

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.amount").value(amounts[i].doubleValue()))
                    .andExpect(jsonPath("$.status").value(statuses[i]));
        }

        // Verificăm că toate cele 5 plăți au fost salvate
        assertThat(paymentRepository.count()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void testGetAllPayments() throws Exception {
        // Arrange
        Reservation savedReservation = createReservation("all");

        // Creăm 3 plăți
        Payment p1 = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(100))
                .status("PAID")
                .paidAt(OffsetDateTime.now())
                .build();
        paymentRepository.save(p1);

        Payment p2 = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(200))
                .status("PENDING")
                .build();
        paymentRepository.save(p2);

        Payment p3 = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(50))
                .status("FAILED")
                .build();
        paymentRepository.save(p3);

        // Act & Assert
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testGetPaymentById() throws Exception {
        // Arrange
        Reservation savedReservation = createReservation("byid");

        Payment payment = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(75.50))
                .status("PAID")
                .paidAt(OffsetDateTime.now())
                .build();
        Payment saved = paymentRepository.save(payment);

        // Act & Assert
        mockMvc.perform(get("/api/payments/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(75.50))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testCreatePayment() throws Exception {
        // Arrange
        Reservation savedReservation = createReservation("create");

        PaymentDTO dto = new PaymentDTO();
        dto.setReservationId(savedReservation.getId());
        dto.setAmount(BigDecimal.valueOf(150.00));
        dto.setStatus("PAID");
        dto.setPaidAt(OffsetDateTime.now());

        // Act
        String response = mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert - verificăm persistența
        PaymentDTO created = objectMapper.readValue(response, PaymentDTO.class);
        Payment paymentInDb = paymentRepository.findById(created.getId()).orElse(null);

        assertThat(paymentInDb).isNotNull();
        assertThat(paymentInDb.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(paymentInDb.getStatus()).isEqualTo("PAID");
    }

    @Test
    void testUpdatePayment() throws Exception {
        // Arrange
        Reservation savedReservation = createReservation("update");

        Payment payment = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(100))
                .status("PENDING")
                .build();
        Payment saved = paymentRepository.save(payment);

        PaymentDTO dto = new PaymentDTO();
        dto.setReservationId(savedReservation.getId());
        dto.setAmount(BigDecimal.valueOf(120));
        dto.setStatus("PAID");
        dto.setPaidAt(OffsetDateTime.now());

        // Act
        mockMvc.perform(put("/api/payments/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(120.00))
                .andExpect(jsonPath("$.status").value("PAID"));

        // Assert
        Payment updated = paymentRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(120));
        assertThat(updated.getStatus()).isEqualTo("PAID");
    }

    @Test
    void testPatchPayment() throws Exception {
        // Arrange
        Reservation savedReservation = createReservation("patch");

        Payment payment = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(100))
                .status("PENDING")
                .build();
        Payment saved = paymentRepository.save(payment);

        PaymentDTO dto = new PaymentDTO();
        dto.setStatus("PAID");

        // Act
        mockMvc.perform(patch("/api/payments/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // Assert
        Payment patched = paymentRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getStatus()).isEqualTo("PAID");
        assertThat(patched.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void testDeletePayment() throws Exception {
        // Arrange
        Reservation savedReservation = createReservation("delete");

        Payment payment = Payment.builder()
                .reservation(savedReservation)
                .amount(BigDecimal.valueOf(50))
                .status("CANCELLED")
                .build();
        Payment saved = paymentRepository.save(payment);

        // Act
        mockMvc.perform(delete("/api/payments/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(paymentRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeletePaymentNotFound() throws Exception {
        // Arrange
        java.util.UUID nonExistentId = java.util.UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/payments/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}