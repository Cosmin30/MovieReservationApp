package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    private TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setPrice(ticket.getPrice());
        dto.setReservationId(ticket.getReservation().getId());
        dto.setSeatId(ticket.getSeat().getId());
        return dto;
    }

    private Ticket toEntity(TicketDTO dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        Seat seat = seatRepository.findById(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setReservation(reservation);
        ticket.setSeat(seat);
        ticket.setPrice(dto.getPrice());
        return ticket;
    }

    @GetMapping
    public List<TicketDTO> getAll() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TicketDTO getById(@PathVariable UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toDTO(ticket);
    }

    @PostMapping
    public TicketDTO create(@RequestBody TicketDTO dto) {
        Ticket ticket = toEntity(dto);
        ticket.setId(null);
        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }

    @PutMapping("/{id}")
    public TicketDTO update(@PathVariable UUID id, @RequestBody TicketDTO dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        Seat seat = seatRepository.findById(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        ticket.setReservation(reservation);
        ticket.setSeat(seat);
        ticket.setPrice(dto.getPrice());

        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found");
        }
        ticketRepository.deleteById(id);
    }
}
