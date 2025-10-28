package com.example.MovieReservationApp.domain.service;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputationServiceTest {

    private ComputationServiceImpl computationService;

    @BeforeEach

    void setUp() {

        computationService = new ComputationServiceImpl();

    }

    @Test

    void shouldCalculateCorrectTotalPrice() {

        double result = computationService.calculateTotalPrice(25.5, 2);

        assertEquals(51.0, result);

    }

    @Test

    void shouldThrowWhenNegativePrice() {

        assertThrows(IllegalArgumentException.class, () -> computationService.calculateTotalPrice(-5, 2));

    }

    @Test

    void shouldThrowWhenZeroQuantity() {

        assertThrows(IllegalArgumentException.class, () -> computationService.calculateTotalPrice(10, 0));

    }

}
