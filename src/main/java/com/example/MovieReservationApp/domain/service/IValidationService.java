package com.example.MovieReservationApp.domain.service;

import java.util.List;
import java.util.UUID;

public interface IValidationService {
    void validateEmail(String email);
    void validateSeatAvailability(boolean isAvailable);
    void validateSeats(List<UUID> seatIds);

}
