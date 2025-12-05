package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "movie", "hall", "startTime", "roomNumber", "capacity", "seats"})
public class ScreeningDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("movie")
    @JsonIgnoreProperties({"screenings"})
    private MovieDTO movie;

    @JsonProperty("hall")
    @JsonIgnoreProperties({"screenings"})
    private HallDTO hall;

    @JsonProperty("start_time")
    @JsonAlias({"startTime", "start_time"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime startTime;

    @JsonProperty("room_number")
    @JsonAlias({"roomNumber", "room_number"})
    private Integer roomNumber;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("seats")
    @JsonIgnoreProperties({"screening"})
    private List<SeatDTO> seats;
}
