package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonRootName("screening")
public class ScreeningDTO {
    private UUID id;
    private MovieDTO movie;
    private HallDTO hall;
    private OffsetDateTime startTime;
    private Integer roomNumber;
    private Integer capacity;

    @JsonManagedReference
    private List<SeatDTO> seats;
}
