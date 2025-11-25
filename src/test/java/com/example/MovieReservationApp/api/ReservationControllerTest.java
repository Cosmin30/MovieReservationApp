package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.application.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllReservations() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(UUID.randomUUID());
        dto.setStatus("CREATED");
        dto.setTotalPrice(BigDecimal.valueOf(100));

        Mockito.when(reservationService.getAllReservations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[0].totalPrice").value(100));
    }

    @Test
    void testGetReservationById() throws Exception {
        UUID id = UUID.randomUUID();
        ReservationDTO dto = new ReservationDTO();
        dto.setId(id);
        dto.setStatus("CREATED");
        dto.setTotalPrice(BigDecimal.valueOf(100));

        Mockito.when(reservationService.getReservationById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/reservations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void testGetReservationsByUser() throws Exception {
        UUID userId = UUID.randomUUID();
        ReservationDTO dto = new ReservationDTO();
        dto.setId(UUID.randomUUID());
        dto.setStatus("CREATED");
        dto.setTotalPrice(BigDecimal.valueOf(100));

        Mockito.when(reservationService.getReservationsByUser(userId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/reservations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[0].totalPrice").value(100));
    }

    @Test
    void testCreateReservation() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID screeningId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(50);

        ReservationDTO dto = new ReservationDTO();
        dto.setId(UUID.randomUUID());
        dto.setStatus("CREATED");
        dto.setTotalPrice(price);

        Mockito.when(reservationService.createReservation(eq(userId), eq(screeningId), anyList(), eq(price)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/reservations")
                        .param("userId", userId.toString())
                        .param("screeningId", screeningId.toString())
                        .param("seatIds", seatId.toString())
                        .param("pricePerSeat", price.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalPrice").value(50));

        verify(reservationService).createReservation(eq(userId), eq(screeningId), anyList(), eq(price));
    }

    @Test
    void testUpdateReservation() throws Exception {
        UUID id = UUID.randomUUID();
        ReservationDTO dto = new ReservationDTO();
        dto.setStatus("PAID");
        dto.setTotalPrice(BigDecimal.valueOf(100));

        ReservationDTO updated = new ReservationDTO();
        updated.setId(id);
        updated.setStatus("PAID");
        updated.setTotalPrice(BigDecimal.valueOf(100));

        Mockito.when(reservationService.updateReservation(eq(id), any(ReservationDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/reservations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.totalPrice").value(100));

        UUID nonExistentId = UUID.randomUUID();
        Mockito.when(reservationService.updateReservation(eq(nonExistentId), any(ReservationDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        mockMvc.perform(put("/api/reservations/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchReservation() throws Exception {
        UUID id = UUID.randomUUID();
        ReservationDTO dto = new ReservationDTO();
        dto.setStatus("CANCELLED");

        ReservationDTO patched = new ReservationDTO();
        patched.setId(id);
        patched.setStatus("CANCELLED");

        Mockito.when(reservationService.patchReservation(eq(id), any(ReservationDTO.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/reservations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        UUID nonExistentId = UUID.randomUUID();
        Mockito.when(reservationService.patchReservation(eq(nonExistentId), any(ReservationDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        mockMvc.perform(patch("/api/reservations/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteReservation() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(reservationService).deleteReservation(id);

        mockMvc.perform(delete("/api/reservations/{id}", id))
                .andExpect(status().isNoContent());

        verify(reservationService).deleteReservation(id);
    }
}
