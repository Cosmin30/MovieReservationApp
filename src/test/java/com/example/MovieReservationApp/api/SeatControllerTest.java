package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeatRepository seatRepository;

    @MockitoBean
    private ScreeningRepository screeningRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- GET ALL ----------
    @Test
    void testGetAll() throws Exception {
        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Seat s1 = new Seat(UUID.randomUUID(), screening, "A", 10, true);
        Seat s2 = new Seat(UUID.randomUUID(), screening, "B", 20, false);

        Mockito.when(seatRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/api/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].row").value("A"))
                .andExpect(jsonPath("$[1].number").value(20));
    }

    // ---------- GET BY ID ----------
    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Seat seat = new Seat(id, screening, "C", 5, true);

        Mockito.when(seatRepository.findById(id)).thenReturn(Optional.of(seat));

        mockMvc.perform(get("/api/seats/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row").value("C"))
                .andExpect(jsonPath("$.number").value(5))
                .andExpect(jsonPath("$.screeningId").value(screening.getId().toString()));
    }

    // ---------- CREATE ----------
    @Test
    void testCreate() throws Exception {
        UUID screeningId = UUID.randomUUID();

        Screening screening = new Screening();
        screening.setId(screeningId);

        SeatDTO dto = new SeatDTO();
        dto.setRow("A");
        dto.setNumber(15);
        dto.setIsAvailable(true);
        dto.setScreeningId(screeningId);

        Seat saved = new Seat(UUID.randomUUID(), screening, "A", 15, true);

        Mockito.when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        Mockito.when(seatRepository.save(any(Seat.class))).thenReturn(saved);

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(15))
                .andExpect(jsonPath("$.row").value("A"));
    }

    // ---------- UPDATE ----------
    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        UUID screeningId = UUID.randomUUID();

        Screening screening = new Screening();
        screening.setId(screeningId);

        Seat existing = new Seat(id, screening, "A", 10, true);

        Seat updated = new Seat(id, screening, "Z", 99, false);

        SeatDTO dto = new SeatDTO();
        dto.setRow("Z");
        dto.setNumber(99);
        dto.setIsAvailable(false);
        dto.setScreeningId(screeningId);

        Mockito.when(seatRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        Mockito.when(seatRepository.save(any(Seat.class))).thenReturn(updated);

        mockMvc.perform(put("/api/seats/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row").value("Z"))
                .andExpect(jsonPath("$.number").value(99))
                .andExpect(jsonPath("$.isAvailable").value(false));
    }

    // ---------- PATCH ----------
    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        Screening screening = new Screening();
        screening.setId(UUID.randomUUID());

        Seat existing = new Seat(id, screening, "A", 10, true);
        Seat patched = new Seat(id, screening, "A", 10, false);

        SeatDTO dto = new SeatDTO();
        dto.setIsAvailable(false);

        Mockito.when(seatRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(seatRepository.save(any(Seat.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/seats/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }

    // ---------- DELETE ----------
    @Test
    void testDelete() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/seats/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(seatRepository).deleteById(id);
    }
}
