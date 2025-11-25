package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.application.service.HallService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HallController.class)
class HallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HallService hallService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        HallDTO h1 = new HallDTO();
        h1.setId(UUID.randomUUID());
        h1.setNumber(1);
        h1.setCapacity(100);
        h1.setName("A");

        HallDTO h2 = new HallDTO();
        h2.setId(UUID.randomUUID());
        h2.setNumber(2);
        h2.setCapacity(200);
        h2.setName("B");

        Mockito.when(hallService.getAllHalls()).thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/api/halls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[1].capacity").value(200));
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        HallDTO hall = new HallDTO();
        hall.setId(id);
        hall.setNumber(5);
        hall.setCapacity(300);
        hall.setName("VIP Room");

        Mockito.when(hallService.getHallById(id)).thenReturn(hall);

        mockMvc.perform(get("/api/halls/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("VIP Room"))
                .andExpect(jsonPath("$.capacity").value(300));
    }

    @Test
    void testCreate() throws Exception {
        HallDTO dto = new HallDTO();
        dto.setName("Test Hall");
        dto.setNumber(7);
        dto.setCapacity(250);

        HallDTO saved = new HallDTO();
        saved.setId(UUID.randomUUID());
        saved.setName("Test Hall");
        saved.setNumber(7);
        saved.setCapacity(250);

        Mockito.when(hallService.createHall(any(HallDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/halls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(7))
                .andExpect(jsonPath("$.capacity").value(250));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();

        HallDTO dto = new HallDTO();
        dto.setId(id);
        dto.setName("Updated");
        dto.setNumber(8);
        dto.setCapacity(400);

        HallDTO updated = new HallDTO();
        updated.setId(id);
        updated.setName("Updated");
        updated.setNumber(8);
        updated.setCapacity(400);

        Mockito.when(hallService.updateHall(eq(id), any(HallDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/halls/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.capacity").value(400));
    }

    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        HallDTO dto = new HallDTO();
        dto.setName("Patched");
        dto.setCapacity(200);

        HallDTO modified = new HallDTO();
        modified.setId(id);
        modified.setNumber(3);
        modified.setCapacity(200);
        modified.setName("Patched");

        Mockito.when(hallService.patchHall(eq(id), any(HallDTO.class))).thenReturn(modified);

        mockMvc.perform(patch("/api/halls/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patched"))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(hallService).deleteHall(id);

        mockMvc.perform(delete("/api/halls/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(hallService).deleteHall(id);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Hall not found"))
                .when(hallService).deleteHall(id);

        mockMvc.perform(delete("/api/halls/{id}", id))
                .andExpect(status().isNotFound());
    }
}