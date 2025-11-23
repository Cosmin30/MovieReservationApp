package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setReservationId(payment.getReservation().getId());
        dto.setStatus(payment.getStatus());
        dto.setPaidAt(payment.getPaidAt());
        dto.setAmount(payment.getAmount());
        return dto;
    }

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

    @GetMapping
    public List<PaymentDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PaymentDTO getById(@PathVariable UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toDTO(payment);
    }

    @PostMapping
    public PaymentDTO create(@RequestBody PaymentDTO dto) {
        Payment payment = toEntity(dto);
        payment.setId(null);
        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

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

    @PatchMapping("/{id}")
    public PaymentDTO patch(@PathVariable UUID id, @RequestBody PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (dto.getStatus() != null) {
            payment.setStatus(dto.getStatus());
        }
        if (dto.getPaidAt() != null) {
            payment.setPaidAt(dto.getPaidAt());
        }
        if (dto.getAmount() != null) {
            payment.setAmount(dto.getAmount());
        }

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found");
        }
        paymentRepository.deleteById(id);
    }
}
