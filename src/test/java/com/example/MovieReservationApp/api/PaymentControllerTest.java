package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.PaymentDTO;
import com.example.MovieReservationApp.application.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllPayments() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(UUID.randomUUID());
        dto.setReservationId(UUID.randomUUID());
        dto.setAmount(BigDecimal.valueOf(50.0));
        dto.setStatus("PAID");
        dto.setPaidAt(OffsetDateTime.now());

        Mockito.when(paymentService.getAllPayments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }

    @Test
    void testGetPaymentById() throws Exception {
        UUID id = UUID.randomUUID();

        PaymentDTO dto = new PaymentDTO();
        dto.setId(id);
        dto.setReservationId(UUID.randomUUID());
        dto.setAmount(BigDecimal.valueOf(75.0));
        dto.setStatus("PENDING");

        Mockito.when(paymentService.getPaymentById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(75.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreatePayment() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setReservationId(UUID.randomUUID());
        dto.setAmount(BigDecimal.valueOf(100.0));

        PaymentDTO saved = new PaymentDTO();
        saved.setId(UUID.randomUUID());
        saved.setReservationId(dto.getReservationId());
        saved.setAmount(dto.getAmount());
        saved.setStatus("PAID");
        saved.setPaidAt(OffsetDateTime.now());

        Mockito.when(paymentService.createPayment(any(PaymentDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testUpdatePayment() throws Exception {
        UUID id = UUID.randomUUID();

        PaymentDTO dto = new PaymentDTO();
        dto.setReservationId(UUID.randomUUID());
        dto.setAmount(BigDecimal.valueOf(120.0));
        dto.setStatus("PAID");

        PaymentDTO updated = new PaymentDTO();
        updated.setId(id);
        updated.setReservationId(dto.getReservationId());
        updated.setAmount(dto.getAmount());
        updated.setStatus(dto.getStatus());

        Mockito.when(paymentService.updatePayment(eq(id), any(PaymentDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(120.0))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testPatchPayment() throws Exception {
        UUID id = UUID.randomUUID();

        PaymentDTO dto = new PaymentDTO();
        dto.setAmount(BigDecimal.valueOf(150.0));

        PaymentDTO patched = new PaymentDTO();
        patched.setId(id);
        patched.setReservationId(UUID.randomUUID());
        patched.setAmount(dto.getAmount());
        patched.setStatus("PAID");

        Mockito.when(paymentService.patchPayment(eq(id), any(PaymentDTO.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    void testDeletePayment() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(paymentService).deletePayment(id);

        mockMvc.perform(delete("/api/payments/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(paymentService).deletePayment(id);
    }

    @Test
    void testDeletePaymentNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Payment not found"))
                .when(paymentService).deletePayment(id);

        mockMvc.perform(delete("/api/payments/{id}", id))
                .andExpect(status().isNotFound());
    }
}
