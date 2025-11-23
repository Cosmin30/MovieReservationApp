package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentDTO payReservation(UUID reservationId, PaymentDTO dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(dto.getAmount());
        payment.setPaidAt(OffsetDateTime.now());
        payment.setStatus("PAID");

        paymentRepository.save(payment);

        PaymentDTO result = new PaymentDTO();
        result.setId(payment.getId());
        result.setReservationId(reservationId);
        result.setAmount(payment.getAmount());
        result.setStatus(payment.getStatus());
        result.setPaidAt(payment.getPaidAt());
        return result;
    }
}
