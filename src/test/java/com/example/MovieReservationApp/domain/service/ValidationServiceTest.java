package com.example.MovieReservationApp.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl() {
            @Override
            public void validateSeats(List<UUID> seatIds) {

            }
        };
    }

    @Test
    void shouldPassWhenEmailIsValid() {
        assertDoesNotThrow(() -> validationService.validateEmail("user@example.com"));
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateEmail("invalidEmail"));
    }

    @Test
    void shouldThrowWhenSeatNotAvailable() {
        assertThrows(IllegalStateException.class, () -> validationService.validateSeatAvailability(false));
    }

    @Test
    void shouldPassWhenSeatAvailable() {
        assertDoesNotThrow(() -> validationService.validateSeatAvailability(true));
    }
}
