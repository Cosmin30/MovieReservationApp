package com.example.MovieReservationApp.domain.service;

import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ValidationServiceImpl implements IValidationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");


    public void validateEmail(User user) {
        if (user == null || user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Email invalid!");
        }
    }

    public void validateSeatAvailability(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Loc inexistent!");
        }
        if (seat.getIsAvailable() == null || !seat.getIsAvailable()) {
            throw new IllegalStateException("Locul nu este disponibil!");
        }
    }

    public void validateSeatsAvailability(Iterable<Seat> seats) {
        for (Seat seat : seats) {
            validateSeatAvailability(seat);
        }
    }


    @Override
    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email invalid!");
        }
    }

    @Override
    public void validateSeatAvailability(boolean isAvailable) {
        if (!isAvailable) {
            throw new IllegalStateException("Locul nu este disponibil!");
        }
    }

    @Override
    public void validateSeats(List<UUID> seatIds) {

    }
}
