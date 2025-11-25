package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.application.service.SeatService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeatService seatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllSeats() throws Exception {
        SeatDTO dto = new SeatDTO();
        dto.setId(UUID.randomUUID());
        dto.setNumber(1);
        dto.setRow("A");
        dto.setIsAvailable(true);

        Mockito.when(seatService.getAllSeats()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(1));
    }

    @Test
    void testGetSeatById() throws Exception {
        UUID id = UUID.randomUUID();
        SeatDTO dto = new SeatDTO();
        dto.setId(id);
        dto.setNumber(2);

        Mockito.when(seatService.getSeatById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/seats/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2));
    }

    @Test
    void testCreateSeat() throws Exception {
        SeatDTO dto = new SeatDTO();
        dto.setNumber(3);
        dto.setRow("B");
        dto.setIsAvailable(true);

        SeatDTO saved = new SeatDTO();
        saved.setId(UUID.randomUUID());
        saved.setNumber(3);
        saved.setRow("B");
        saved.setIsAvailable(true);

        Mockito.when(seatService.createSeat(any(SeatDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(3));
    }

    @Test
    void testUpdateSeat() throws Exception {
        UUID id = UUID.randomUUID();
        SeatDTO dto = new SeatDTO();
        dto.setNumber(4);

        SeatDTO updated = new SeatDTO();
        updated.setId(id);
        updated.setNumber(4);

        Mockito.when(seatService.updateSeat(eq(id), any(SeatDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/seats/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(4));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(seatService.updateSeat(eq(nonExistent), any(SeatDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        mockMvc.perform(put("/api/seats/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchSeat() throws Exception {
        UUID id = UUID.randomUUID();
        SeatDTO dto = new SeatDTO();
        dto.setNumber(5);

        SeatDTO patched = new SeatDTO();
        patched.setId(id);
        patched.setNumber(5);

        Mockito.when(seatService.patchSeat(eq(id), any(SeatDTO.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/seats/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(5));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(seatService.patchSeat(eq(nonExistent), any(SeatDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        mockMvc.perform(patch("/api/seats/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteSeat() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(seatService).deleteSeat(id);

        mockMvc.perform(delete("/api/seats/{id}", id))
                .andExpect(status().isNoContent());

        verify(seatService).deleteSeat(id);
    }
}
