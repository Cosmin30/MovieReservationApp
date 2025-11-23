package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonRootName("ticket")
public class TicketDTO {
    private UUID id;
    private BigDecimal price;
    private SeatDTO seat;
}
