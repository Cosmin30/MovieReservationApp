package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.api.dto.PaymentDTO;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // ENTITY → DTO
    private PaymentDTO toDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getReservation().getId(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getAmount()
        );
    }

    // DTO → ENTITY
    private Payment toEntity(PaymentDTO dto) {

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setReservation(reservation);
        payment.setStatus(dto.getStatus());
        payment.setPaidAt(dto.getPaidAt());
        payment.setAmount(dto.getAmount());

        return payment;
    }

    // GET ALL
    @GetMapping
    public List<PaymentDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public PaymentDTO getById(@PathVariable UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return toDTO(payment);
    }

    // CREATE
    @PostMapping
    public PaymentDTO create(@RequestBody PaymentDTO dto) {
        Payment payment = toEntity(dto);
        payment.setId(null);

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    // UPDATE
    @PutMapping("/{id}")
    public PaymentDTO update(@PathVariable UUID id, @RequestBody PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        payment.setReservation(reservation);
        payment.setStatus(dto.getStatus());
        payment.setPaidAt(dto.getPaidAt());
        payment.setAmount(dto.getAmount());

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        paymentRepository.deleteById(id);
    }
}
