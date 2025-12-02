package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "amount", "paidAt", "status", "reservationId"})
public class PaymentDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("paid_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime paidAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("reservation_id")
    private UUID reservationId;
}