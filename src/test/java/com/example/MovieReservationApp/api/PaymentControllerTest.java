package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());

        Payment p1 = new Payment();
        p1.setId(UUID.randomUUID());
        p1.setReservation(reservation);
        p1.setStatus("PAID");
        p1.setPaidAt(OffsetDateTime.now());
        p1.setAmount(new BigDecimal("30.00"));

        Payment p2 = new Payment();
        p2.setId(UUID.randomUUID());
        p2.setReservation(reservation);
        p2.setStatus("PENDING");
        p2.setPaidAt(OffsetDateTime.now());
        p2.setAmount(new BigDecimal("45.00"));

        Mockito.when(paymentRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[1].amount").value(45.00));
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());

        Payment payment = new Payment();
        payment.setId(id);
        payment.setReservation(reservation);
        payment.setStatus("PAID");
        payment.setPaidAt(OffsetDateTime.now());
        payment.setAmount(new BigDecimal("99.99"));

        Mockito.when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        mockMvc.perform(get("/api/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.amount").value(99.99));
    }

    @Test
    void testCreate() throws Exception {
        UUID reservationId = UUID.randomUUID();

        PaymentDTO dto = new PaymentDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setPaidAt(OffsetDateTime.now());
        dto.setStatus("PAID");
        dto.setReservationId(reservationId);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        Payment saved = new Payment();
        saved.setId(UUID.randomUUID());
        saved.setReservation(reservation);
        saved.setStatus(dto.getStatus());
        saved.setPaidAt(dto.getPaidAt());
        saved.setAmount(dto.getAmount());

        Mockito.when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        Payment existing = new Payment();
        existing.setId(id);
        existing.setReservation(reservation);
        existing.setStatus("PENDING");
        existing.setPaidAt(OffsetDateTime.now());
        existing.setAmount(new BigDecimal("10.00"));

        Payment updated = new Payment();
        updated.setId(id);
        updated.setReservation(reservation);
        updated.setStatus("PAID");
        updated.setPaidAt(OffsetDateTime.now());
        updated.setAmount(new BigDecimal("100.00"));

        PaymentDTO dto = new PaymentDTO();
        dto.setId(id);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPaidAt(OffsetDateTime.now());
        dto.setStatus("PAID");
        dto.setReservationId(reservationId);

        Mockito.when(paymentRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(updated);

        mockMvc.perform(put("/api/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testPatch() throws Exception {
        UUID id = UUID.randomUUID();

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());

        Payment existing = new Payment();
        existing.setId(id);
        existing.setReservation(reservation);
        existing.setStatus("PENDING");
        existing.setPaidAt(OffsetDateTime.now());
        existing.setAmount(new BigDecimal("15.00"));

        Payment patched = new Payment();
        patched.setId(id);
        patched.setReservation(reservation);
        patched.setStatus("PAID");
        patched.setPaidAt(existing.getPaidAt());
        patched.setAmount(existing.getAmount());

        PaymentDTO dto = new PaymentDTO();
        dto.setStatus("PAID");

        Mockito.when(paymentRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(paymentRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/payments/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(paymentRepository).deleteById(id);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(paymentRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/payments/{id}", id))
                .andExpect(status().is4xxClientError());
    }
}
