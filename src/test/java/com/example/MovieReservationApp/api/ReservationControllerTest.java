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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ScreeningRepository screeningRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- GET ALL ----------
    @Test
    void testGetAll() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Reservation r1 = new Reservation(UUID.randomUUID(), user, screening,
                OffsetDateTime.now(), "ACTIVE", new BigDecimal("30.00"), null, null);

        Reservation r2 = new Reservation(UUID.randomUUID(), user, screening,
                OffsetDateTime.now(), "PAID", new BigDecimal("50.00"), null, null);

        Mockito.when(reservationRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/reservations"))
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

        Reservation reservation = new Reservation(
                id, user, screening,
                OffsetDateTime.now(),
                "CONFIRMED",
                new BigDecimal("150.00"),
                null,
                null
        );

        Mockito.when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/api/reservations/{id}", id))
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

        ReservationDTO dto = new ReservationDTO();
        dto.setUser(new UserDTO(userId, null, null, null, null));
        dto.setScreening(new ScreeningDTO(screeningId, null, null, null, null, null));
        dto.setStatus("NEW");
        dto.setTotalPrice(new BigDecimal("99.99"));
        dto.setCreatedAt(OffsetDateTime.now());

        Reservation saved = new Reservation(
                UUID.randomUUID(),
                user,
                screening,
                dto.getCreatedAt(),
                "NEW",
                dto.getTotalPrice(),
                null,
                null
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(99.99));
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

        Reservation existing = new Reservation(
                id, user, screening, OffsetDateTime.now(),
                "OLD", new BigDecimal("10.00"), null, null
        );

        Reservation updated = new Reservation(
                id, user, screening, OffsetDateTime.now(),
                "UPDATED", new BigDecimal("200.00"), null, null
        );

        ReservationDTO dto = new ReservationDTO();
        dto.setUser(new UserDTO(userId, null, null, null, null));
        dto.setScreening(new ScreeningDTO(screeningId, null, null, null, null, null));
        dto.setStatus("UPDATED");
        dto.setCreatedAt(updated.getCreatedAt());
        dto.setTotalPrice(new BigDecimal("200.00"));

        Mockito.when(reservationRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenReturn(updated);

        mockMvc.perform(put("/api/reservations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"))
                .andExpect(jsonPath("$.totalPrice").value(200.00));
    }

    // ---------- PATCH ----------
    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        User user = new User();
        user.setId(UUID.randomUUID());

        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Reservation existing = new Reservation(
                id, user, screening,
                OffsetDateTime.now(),
                "OLD",
                new BigDecimal("10.00"),
                null, null
        );

        Reservation patched = new Reservation(
                id, user, screening,
                existing.getCreatedAt(),
                "PATCHED",
                new BigDecimal("10.00"),
                null, null
        );

        ReservationDTO dto = new ReservationDTO();
        dto.setStatus("PATCHED");

        Mockito.when(reservationRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/reservations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PATCHED"));
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

    // ---------- DELETE ERROR ----------
    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(reservationRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/reservations/{id}", id))
                .andExpect(status().is4xxClientError());
    }
}
