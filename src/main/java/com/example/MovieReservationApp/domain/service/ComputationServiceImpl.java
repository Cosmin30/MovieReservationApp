package com.example.MovieReservationApp.domain.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class ComputationServiceImpl implements IComputationService {

    public BigDecimal calculateTotalPrice(BigDecimal ticketPrice, int quantity) {
        if (ticketPrice == null || ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Prețul trebuie să fie pozitiv!");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cantitatea trebuie să fie pozitivă!");
        }

        BigDecimal total = ticketPrice.multiply(BigDecimal.valueOf(quantity));

        if (quantity >= 5) {
            BigDecimal discount = total.multiply(BigDecimal.valueOf(0.1)); // reducere 10%
            total = total.subtract(discount);
        }
        return total;
    }

    @Override
    public double calculateTotalPrice(double ticketPrice, int quantity) {
        return calculateTotalPrice(BigDecimal.valueOf(ticketPrice), quantity).doubleValue();
    }
}
