package com.example.MovieReservationApp.domain.service;

import org.springframework.stereotype.Service;

@Service
public class ComputationServiceImpl implements IComputationService {

    @Override
    public double calculateTotalPrice(double ticketPrice, int quantity) {
        if (ticketPrice < 0 || quantity <= 0) {
            throw new IllegalArgumentException("Prețul și cantitatea trebuie să fie pozitive!");
        }
        return ticketPrice * quantity;
    }
}

