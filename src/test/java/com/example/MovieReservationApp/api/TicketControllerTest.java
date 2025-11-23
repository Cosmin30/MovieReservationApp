package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private SeatRepository seatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- GET ALL ----------
    @Test
    void testGetAll() throws Exception {
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());

        Seat s = new Seat();
        s.setId(UUID.randomUUID());

        Ticket t1 = new Ticket(UUID.randomUUID(), r, s, new BigDecimal("15.00"));
        Ticket t2 = new Ticket(UUID.randomUUID(), r, s, new BigDecimal("20.00"));

        Mockito.when(ticketRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(15.00))
                .andExpect(jsonPath("$[1].price").value(20.00));
    }

    // ---------- GET BY ID ----------
    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());

        Seat s = new Seat();
        s.setId(UUID.randomUUID());

        Ticket ticket = new Ticket(id, r, s, new BigDecimal("50.00"));

        Mockito.when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(50.00))
                .andExpect(jsonPath("$.reservationId").value(r.getId().toString()))
                .andExpect(jsonPath("$.seatId").value(s.getId().toString()));
    }

    // ---------- CREATE ----------
    @Test
    void testCreate() throws Exception {
        UUID reservationId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        Seat seat = new Seat();
        seat.setId(seatId);

        TicketDTO dto = new TicketDTO();
        dto.setPrice(new BigDecimal("25.00"));
        dto.setReservationId(reservationId);
        dto.setSeatId(seatId);

        Ticket saved = new Ticket(UUID.randomUUID(), reservation, seat, new BigDecimal("25.00"));

        Mockito.when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        Mockito.when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        Mockito.when(ticketRepository.save(any(Ticket.class))).thenReturn(saved);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(25.00))
                .andExpect(jsonPath("$.reservationId").value(reservationId.toString()))
                .andExpect(jsonPath("$.seatId").value(seatId.toString()));
    }

    // ---------- UPDATE ----------
    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        UUID resId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        Reservation reservation = new Reservation();
        reservation.setId(resId);

        Seat seat = new Seat();
        seat.setId(seatId);

        Ticket existing = new Ticket(id, reservation, seat, new BigDecimal("10.00"));
        Ticket updated = new Ticket(id, reservation, seat, new BigDecimal("99.99"));

        TicketDTO dto = new TicketDTO();
        dto.setReservationId(resId);
        dto.setSeatId(seatId);
        dto.setPrice(new BigDecimal("99.99"));

        Mockito.when(ticketRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));
        Mockito.when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        Mockito.when(ticketRepository.save(any(Ticket.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(99.99));
    }

    // ---------- DELETE SUCCESS ----------
    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(ticketRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/tickets/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(ticketRepository).deleteById(id);
    }

    // ---------- DELETE NOT FOUND ----------
    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(ticketRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/tickets/{id}", id))
                .andExpect(status().is4xxClientError());
    }
}
