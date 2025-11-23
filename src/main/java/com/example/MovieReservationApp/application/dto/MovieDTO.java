package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonRootName("movie")
public class MovieDTO {
    private UUID id;
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private LocalDate releaseDate;
}
