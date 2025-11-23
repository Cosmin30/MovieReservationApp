package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<TicketDTO> getTicketsByReservation(UUID reservationId) {
        return ticketRepository.findByReservationId(reservationId).stream().map(ticket -> {
            TicketDTO dto = new TicketDTO();
            dto.setId(ticket.getId());
            dto.setReservationId(reservationId);
            dto.setSeatId(ticket.getSeat().getId());
            dto.setPrice(ticket.getPrice());
            return dto;
        }).collect(Collectors.toList());
    }
}
