package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.application.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public TicketDTO getTicketById(@PathVariable UUID id) {
        return ticketService.getTicketById(id);
    }

    @GetMapping("/reservation/{reservationId}")
    public List<TicketDTO> getTicketsByReservation(@PathVariable UUID reservationId) {
        return ticketService.getTicketsByReservation(reservationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDTO createTicket(@RequestBody TicketDTO dto) {
        return ticketService.createTicket(dto);
    }

    @PutMapping("/{id}")
    public TicketDTO updateTicket(@PathVariable UUID id, @RequestBody TicketDTO dto) {
        return ticketService.updateTicket(id, dto);
    }

    @PatchMapping("/{id}")
    public TicketDTO patchTicket(@PathVariable UUID id, @RequestBody TicketDTO dto) {
        return ticketService.patchTicket(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
    }
}
