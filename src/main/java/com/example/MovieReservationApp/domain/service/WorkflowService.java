package com.example.MovieReservationApp.domain.service;
import com.example.MovieReservationApp.domain.factory.ReservationFactory;
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
        // 1. Obține user și screening
        User user = userRepository.findById(userId).orElseThrow();
        Screening screening = screeningRepository.findById(screeningId).orElseThrow();

        // 2. Verifică locurile
        List<Seat> seats = seatRepository.findAllById(seatIds);
        seats.forEach(seat -> validationService.validateSeatAvailability(seat.getIsAvailable()));

        // 3. Calculează total
        double totalPrice = computationService.calculateTotalPrice(ticketPrice, seats.size());

        // 4. Creează rezervare
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("CREATED");
        reservation.setCreatedAt(OffsetDateTime.now());
        reservationRepository.save(reservation);

        // 5. Creează bilete și ocupă locuri
        seats.forEach(seat -> {
            Ticket ticket = new Ticket();
            ticket.setReservation(reservation);
            ticket.setSeat(seat);
            ticket.setPrice(BigDecimal.valueOf(ticketPrice));
            ticketRepository.save(ticket);
            seat.setIsAvailable(false);
            seatRepository.save(seat);
        });

        // 6. Log audit
        auditService.logAction("Rezervare creată pentru user " + user.getId() + " cu total " + totalPrice);

        return reservation;
    }
}
