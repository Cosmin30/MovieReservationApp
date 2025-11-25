package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentDTO getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO createPayment(@RequestBody PaymentDTO dto) {
        return paymentService.createPayment(dto);
    }

    @PutMapping("/{id}")
    public PaymentDTO updatePayment(@PathVariable UUID id, @RequestBody PaymentDTO dto) {
        return paymentService.updatePayment(id, dto);
    }

    @PatchMapping("/{id}")
    public PaymentDTO patchPayment(@PathVariable UUID id, @RequestBody PaymentDTO dto) {
        return paymentService.patchPayment(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
    }
}
