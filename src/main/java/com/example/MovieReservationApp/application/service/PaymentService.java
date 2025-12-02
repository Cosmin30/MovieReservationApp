package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    // ---------------------------------------
    // GET ALL
    // ---------------------------------------
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------------
    // GET BY ID
    // ---------------------------------------
    public PaymentDTO getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        return toDTO(payment);
    }

    // ---------------------------------------
    // CREATE
    // ---------------------------------------
    public PaymentDTO createPayment(PaymentDTO dto) {

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        Payment payment = toEntity(dto);
        payment.setId(null);
        payment.setReservation(reservation);
        payment.setPaidAt(OffsetDateTime.now());
        payment.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    // ---------------------------------------
    // UPDATE (PUT)
    // ---------------------------------------
    public PaymentDTO updatePayment(UUID id, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        payment.setReservation(reservation);
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setPaidAt(dto.getPaidAt());

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    // ---------------------------------------
    // PATCH
    // ---------------------------------------
    public PaymentDTO patchPayment(UUID id, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        if (dto.getReservationId() != null) {
            Reservation reservation = reservationRepository.findById(dto.getReservationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
            payment.setReservation(reservation);
        }

        if (dto.getAmount() != null) payment.setAmount(dto.getAmount());
        if (dto.getStatus() != null) payment.setStatus(dto.getStatus());
        if (dto.getPaidAt() != null) payment.setPaidAt(dto.getPaidAt());

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    // ---------------------------------------
    // DELETE
    // ---------------------------------------
    public void deletePayment(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }
        paymentRepository.deleteById(id);
    }

    // ---------------------------------------
    // DTO CONVERTERS
    // ---------------------------------------
    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setReservationId(payment.getReservation().getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }

    private Payment toEntity(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setPaidAt(dto.getPaidAt());
        return payment;
    }
}
