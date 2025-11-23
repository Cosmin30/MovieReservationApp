package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.UUID;

@Data
@JsonRootName("hall")
public class HallDTO {
    private UUID id;
    private String name;
    private Integer number;
    private Integer capacity;
}
