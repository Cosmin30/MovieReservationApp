package com.example.MovieReservationApp.domain.service;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;

    private final IValidationService validationService;
    private final IComputationService computationService;
    private final IAuditService auditService;

    public Reservation processReservation(UUID userId, UUID screeningId, List<UUID> seatIds, double ticketPrice) {
        User user = userRepository.findById(userId).orElseThrow();
        Screening screening = screeningRepository.findById(screeningId).orElseThrow();

        List<Seat> seats = seatRepository.findAllById(seatIds);
        seats.forEach(seat -> validationService.validateSeatAvailability(seat.getIsAvailable()));

        double totalPrice = computationService.calculateTotalPrice(ticketPrice, seats.size());

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setTotalPrice(BigDecimal.valueOf(totalPrice));
        reservation.setStatus("CREATED");
        reservation.setCreatedAt(OffsetDateTime.now());
        Reservation savedReservation = reservationRepository.save(reservation);

        seats.forEach(seat -> {
            Ticket ticket = new Ticket();
            ticket.setReservation(savedReservation);
            ticket.setSeat(seat);
            ticket.setPrice(BigDecimal.valueOf(ticketPrice));
            ticketRepository.save(ticket);
            seat.setIsAvailable(false);
            seatRepository.save(seat);
        });

        auditService.logAction("Rezervare creată pentru user " + user.getId() + " cu total " + totalPrice);

        return savedReservation;
    }
}