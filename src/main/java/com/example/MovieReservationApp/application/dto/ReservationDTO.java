package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@JsonRootName("reservation")
public class ReservationDTO {
    private UUID id;
    private UserDTO user;
    private ScreeningDTO screening;
    private OffsetDateTime createdAt;
    private String status;
    private BigDecimal totalPrice;
    private List<TicketDTO> tickets;
}
