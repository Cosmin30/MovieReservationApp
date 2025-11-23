package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@JsonRootName("payment")
public class PaymentDTO {
    private UUID id;
    private BigDecimal amount;
    private OffsetDateTime paidAt;
    private String status;
    private UUID reservationId;
}
