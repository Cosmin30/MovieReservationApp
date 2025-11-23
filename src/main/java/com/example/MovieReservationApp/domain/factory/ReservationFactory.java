package com.example.MovieReservationApp.domain.factory;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.user.User;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ReservationFactory {

    public static Reservation createReservation(User user, Screening screening, BigDecimal totalPrice) {
        return Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CREATED")
                .totalPrice(totalPrice)
                .createdAt(OffsetDateTime.now())
                .build();
    }

}
