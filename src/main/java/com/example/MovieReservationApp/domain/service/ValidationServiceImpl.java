package com.example.MovieReservationApp.domain.service;

import org.springframework.stereotype.Service;

@Service
 public class ValidationServiceImpl implements IValidationService {

    @Override
    public void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalid!");
        }
    }

    @Override
    public void validateSeatAvailability(boolean isAvailable) {
        if (!isAvailable) {
            throw new IllegalStateException("Locul nu este disponibil!");
        }
    }
}
