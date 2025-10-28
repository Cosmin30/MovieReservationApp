package com.example.MovieReservationApp.domain.service;
import com.example.MovieReservationApp.domain.factory.ReservationFactory;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final IValidationService validationService;
    private final IComputationService computationService;
    private final IAuditService auditService;
    private final ReservationFactory reservationFactory;

    public Reservation processReservation(UUID userId, UUID screeningId, double ticketPrice, int quantity, String email) {
        validationService.validateEmail(email);
        double totalPrice = computationService.calculateTotalPrice(ticketPrice, quantity);
        Reservation reservation = reservationFactory.createReservation(userId, screeningId, totalPrice);
        auditService.logAction("Rezervare creată pentru user " + userId + " cu total " + totalPrice);
        return reservation;
    }
}