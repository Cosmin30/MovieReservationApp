package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ScreeningRepository screeningRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupMapper() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    // ---------- GET ALL ----------
    @Test
    void testGetAll() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Reservation r1 = new Reservation();
        r1.setId(UUID.randomUUID());
        r1.setUser(user);
        r1.setScreening(screening);
        r1.setStatus("ACTIVE");
        r1.setTotalPrice(new BigDecimal("30.00"));
        r1.setCreatedAt(OffsetDateTime.now());

        Reservation r2 = new Reservation();
        r2.setId(UUID.randomUUID());
        r2.setUser(user);
        r2.setScreening(screening);
        r2.setStatus("PAID");
        r2.setTotalPrice(new BigDecimal("50.00"));
        r2.setCreatedAt(OffsetDateTime.now());

        Mockito.when(reservationRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/reservations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].totalPrice").value(50.00));
    }

    // ---------- GET BY ID ----------
    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setStatus("CONFIRMED");
        reservation.setTotalPrice(new BigDecimal("150.00"));
        reservation.setCreatedAt(OffsetDateTime.now());

        Mockito.when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/api/reservations/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalPrice").value(150.00))
                .andExpect(jsonPath("$.user.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.screening.id").value(screening.getId().toString()));
    }

    // ---------- CREATE ----------
    @Test
    void testCreate() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID screeningId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Screening screening = new Screening();
        screening.setId(screeningId);

        // Folosim Map pentru JSON, ca la PATCH
        Map<String, Object> dtoMap = Map.of(
                "user", Map.of("id", userId.toString()),
                "screening", Map.of("id", screeningId.toString()),
                "status", "NEW",
                "totalPrice", 99.99,
                "createdAt", OffsetDateTime.now().toString()
        );

        Reservation saved = new Reservation();
        saved.setId(UUID.randomUUID());
        saved.setUser(user);
        saved.setScreening(screening);
        saved.setStatus("NEW");
        saved.setTotalPrice(new BigDecimal("99.99"));
        saved.setCreatedAt(OffsetDateTime.now());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(99.99));
    }



    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID screeningId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Screening screening = new Screening();
        screening.setId(screeningId);

        Reservation existing = new Reservation();
        existing.setId(id);
        existing.setUser(user);
        existing.setScreening(screening);
        existing.setStatus("OLD");
        existing.setTotalPrice(new BigDecimal("10.00"));
        existing.setCreatedAt(OffsetDateTime.now());

        Reservation patched = new Reservation();
        patched.setId(id);
        patched.setUser(user);
        patched.setScreening(screening);
        patched.setStatus("PATCHED");
        patched.setTotalPrice(existing.getTotalPrice());
        patched.setCreatedAt(existing.getCreatedAt());

        Mockito.when(reservationRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenReturn(patched);

        Map<String, Object> dto = Map.of("status", "PATCHED");

        mockMvc.perform(patch("/api/reservations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PATCHED"));
    }


    // ---------- UPDATE ----------
    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID screeningId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Screening screening = new Screening();
        screening.setId(screeningId);

        Reservation existing = new Reservation();
        existing.setId(id);
        existing.setUser(user);
        existing.setScreening(screening);
        existing.setStatus("OLD");
        existing.setTotalPrice(new BigDecimal("10.00"));
        existing.setCreatedAt(OffsetDateTime.now());

        Reservation updated = new Reservation();
        updated.setId(id);
        updated.setUser(user);
        updated.setScreening(screening);
        updated.setStatus("UPDATED");
        updated.setTotalPrice(new BigDecimal("200.00"));
        updated.setCreatedAt(existing.getCreatedAt());

        Map<String, Object> dtoMap = Map.of(
                "user", Map.of("id", userId.toString()),
                "screening", Map.of("id", screeningId.toString()),
                "status", "NEW",
                "totalPrice", 99.99,
                "createdAt", OffsetDateTime.now().toString()
        );


        Mockito.when(reservationRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenReturn(updated);

        mockMvc.perform(put("/api/reservations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"))
                .andExpect(jsonPath("$.totalPrice").value(200.00));
    }


    // ---------- DELETE SUCCESS ----------
    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(reservationRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/reservations/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(reservationRepository).deleteById(id);
    }

    // ---------- DELETE NOT FOUND ----------
    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(reservationRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/reservations/{id}", id))
                .andExpect(status().is4xxClientError());
    }
}
