package com.example.MovieReservationApp.domain.factory;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ReservationFactory {

    public Reservation createReservation(UUID userId, UUID screeningId, double totalPrice) {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(userId);
        reservation.setScreeningId(screeningId);
        reservation.setCreatedAt(OffsetDateTime.now());
        reservation.setStatus("CREATED");
        reservation.setTotalPrice(totalPrice);
        return reservation;
    }
}
