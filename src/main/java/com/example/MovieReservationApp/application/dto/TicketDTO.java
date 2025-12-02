package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "price", "reservationId", "seatId"})
public class TicketDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("reservation_id")
    private UUID reservationId;

    @JsonProperty("seat_id")
    private UUID seatId;
}