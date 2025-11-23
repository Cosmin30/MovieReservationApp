package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import java.util.UUID;

@Data
@JsonRootName("seat")
public class SeatDTO {
    private UUID id;
    private Integer number;
    private String row;
    private Boolean isAvailable;
    private UUID screeningId;
}
