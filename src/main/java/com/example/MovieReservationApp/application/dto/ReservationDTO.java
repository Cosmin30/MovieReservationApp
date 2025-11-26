package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "user", "screening", "createdAt", "status", "totalPrice", "tickets"})
public class ReservationDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("user")
    @JsonIgnoreProperties({"reservations"})
    private UserDTO user;

    @JsonProperty("screening")
    @JsonIgnoreProperties({"reservations"})
    private ScreeningDTO screening;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("tickets")
    @JsonIgnoreProperties({"reservation"})
    private List<TicketDTO> tickets;
}