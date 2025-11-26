package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.application.service.ScreeningService;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScreeningController.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScreeningService screeningService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllScreenings() throws Exception {
        ScreeningDTO dto = new ScreeningDTO();
        dto.setId(UUID.randomUUID());
        dto.setStartTime(OffsetDateTime.now());
        dto.setCapacity(100);
        dto.setRoomNumber(1);
        dto.setMovie(new MovieDTO());
        dto.setHall(new HallDTO());
        dto.setSeats(List.of());

        Mockito.when(screeningService.getAllScreenings()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/screenings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].capacity").value(100));
    }

    @Test
    void testGetScreeningById() throws Exception {
        UUID id = UUID.randomUUID();
        ScreeningDTO dto = new ScreeningDTO();
        dto.setId(id);
        dto.setCapacity(150);

        Mockito.when(screeningService.getScreeningById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/screenings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(150));
    }

    @Test
    void testCreateScreening() throws Exception {
        ScreeningDTO dto = new ScreeningDTO();
        dto.setStartTime(OffsetDateTime.now());
        dto.setCapacity(120);

        ScreeningDTO saved = new ScreeningDTO();
        saved.setId(UUID.randomUUID());
        saved.setCapacity(120);

        Mockito.when(screeningService.createScreening(any(ScreeningDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacity").value(120));
    }

    @Test
    void testUpdateScreening() throws Exception {
        UUID id = UUID.randomUUID();
        ScreeningDTO dto = new ScreeningDTO();
        dto.setCapacity(200);

        ScreeningDTO updated = new ScreeningDTO();
        updated.setId(id);
        updated.setCapacity(200);

        Mockito.when(screeningService.updateScreening(eq(id), any(ScreeningDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/screenings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(200));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(screeningService.updateScreening(eq(nonExistent), any(ScreeningDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));

        mockMvc.perform(put("/api/screenings/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchScreening() throws Exception {
        UUID id = UUID.randomUUID();
        ScreeningDTO dto = new ScreeningDTO();
        dto.setCapacity(50);

        ScreeningDTO patched = new ScreeningDTO();
        patched.setId(id);
        patched.setCapacity(50);

        Mockito.when(screeningService.patchScreening(eq(id), any(ScreeningDTO.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/screenings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(50));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(screeningService.patchScreening(eq(nonExistent), any(ScreeningDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));

        mockMvc.perform(patch("/api/screenings/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteScreening() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(screeningService).deleteScreening(id);

        mockMvc.perform(delete("/api/screenings/{id}", id))
                .andExpect(status().isNoContent());

        verify(screeningService).deleteScreening(id);
    }
}
