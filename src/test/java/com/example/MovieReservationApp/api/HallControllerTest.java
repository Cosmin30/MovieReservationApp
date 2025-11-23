package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HallController.class)
class HallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {

        Hall h1 = new Hall(UUID.randomUUID(), 1, 100, "A");
        Hall h2 = new Hall(UUID.randomUUID(), 2, 200, "B");

        Mockito.when(hallRepository.findAll()).thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/api/halls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[1].capacity").value(200));
    }

    @Test
    void testGetById() throws Exception {

        UUID id = UUID.randomUUID();
        Hall hall = new Hall(id, 5, 300, "VIP Room");

        Mockito.when(hallRepository.findById(id)).thenReturn(Optional.of(hall));

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

        Hall saved = new Hall(UUID.randomUUID(), 7, 250, "Test Hall");

        Mockito.when(hallRepository.save(any(Hall.class))).thenReturn(saved);

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

        Hall existing = new Hall(id, 2, 100, "Old");
        Hall updated = new Hall(id, 8, 400, "Updated");

        HallDTO dto = new HallDTO();
        dto.setId(id);
        dto.setName("Updated");
        dto.setNumber(8);
        dto.setCapacity(400);

        Mockito.when(hallRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(hallRepository.save(any(Hall.class))).thenReturn(updated);

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

        Hall existing = new Hall(id, 3, 120, "Old");
        Hall modified = new Hall(id, 3, 200, "Patched");

        HallDTO dto = new HallDTO();
        dto.setName("Patched");
        dto.setCapacity(200);

        Mockito.when(hallRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(hallRepository.save(any(Hall.class))).thenReturn(modified);

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

        Mockito.when(hallRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/halls/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(hallRepository).deleteById(id);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(hallRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/halls/{id}", id))
                .andExpect(status().is4xxClientError());
    }

}
