package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.application.service.TicketService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetTicketsByReservation() throws Exception {
        UUID reservationId = UUID.randomUUID();
        TicketDTO dto = new TicketDTO();
        dto.setId(UUID.randomUUID());
        dto.setReservationId(reservationId);
        dto.setSeatId(UUID.randomUUID());
        dto.setPrice(BigDecimal.valueOf(50));

        Mockito.when(ticketService.getTicketsByReservation(reservationId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tickets/reservation/{reservationId}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(50));
    }

    @Test
    void testCreateTicket() throws Exception {
        TicketDTO dto = new TicketDTO();
        dto.setReservationId(UUID.randomUUID());
        dto.setSeatId(UUID.randomUUID());
        dto.setPrice(BigDecimal.valueOf(75));

        TicketDTO saved = new TicketDTO();
        saved.setId(UUID.randomUUID());
        saved.setReservationId(dto.getReservationId());
        saved.setSeatId(dto.getSeatId());
        saved.setPrice(BigDecimal.valueOf(75));

        Mockito.when(ticketService.createTicket(any(TicketDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(75));
    }

    @Test
    void testUpdateTicket() throws Exception {
        UUID id = UUID.randomUUID();
        TicketDTO dto = new TicketDTO();
        dto.setPrice(BigDecimal.valueOf(80));

        TicketDTO updated = new TicketDTO();
        updated.setId(id);
        updated.setPrice(BigDecimal.valueOf(80));

        Mockito.when(ticketService.updateTicket(eq(id), any(TicketDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(80));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(ticketService.updateTicket(eq(nonExistent), any(TicketDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        mockMvc.perform(put("/api/tickets/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchTicket() throws Exception {
        UUID id = UUID.randomUUID();
        TicketDTO dto = new TicketDTO();
        dto.setPrice(BigDecimal.valueOf(90));

        TicketDTO patched = new TicketDTO();
        patched.setId(id);
        patched.setPrice(BigDecimal.valueOf(90));

        Mockito.when(ticketService.patchTicket(eq(id), any(TicketDTO.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(90));

        UUID nonExistent = UUID.randomUUID();
        Mockito.when(ticketService.patchTicket(eq(nonExistent), any(TicketDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        mockMvc.perform(patch("/api/tickets/{id}", nonExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTicket() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(ticketService).deleteTicket(id);

        mockMvc.perform(delete("/api/tickets/{id}", id))
                .andExpect(status().isNoContent());

        verify(ticketService).deleteTicket(id);
    }
}
