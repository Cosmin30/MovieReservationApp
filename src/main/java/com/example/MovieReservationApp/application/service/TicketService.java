package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByReservation(UUID reservationId) {
        return ticketRepository.findByReservationId(reservationId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO getTicketById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        return toDTO(ticket);
    }

    public TicketDTO createTicket(TicketDTO dto) {
        Ticket ticket = toEntity(dto);
        ticket.setId(null);
        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }

    public TicketDTO updateTicket(UUID id, TicketDTO dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        ticket.setPrice(dto.getPrice());

        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }

    public TicketDTO patchTicket(UUID id, TicketDTO dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (dto.getPrice() != null) ticket.setPrice(dto.getPrice());

        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }

    public void deleteTicket(UUID id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        ticketRepository.deleteById(id);
    }

    private TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setPrice(ticket.getPrice());
        dto.setSeatId(ticket.getSeat() != null ? ticket.getSeat().getId() : null);
        dto.setReservationId(ticket.getReservation() != null ? ticket.getReservation().getId() : null);
        return dto;
    }

    private Ticket toEntity(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setPrice(dto.getPrice());
        return ticket;
    }
}
