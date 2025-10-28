package com.example.MovieReservationApp.domain.service;

public interface IValidationService {
    void validateEmail(String email);
    void validateSeatAvailability(boolean isAvailable);
}
