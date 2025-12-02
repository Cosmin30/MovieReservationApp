package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "number", "row", "isAvailable", "screeningId"})
public class SeatDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("row")
    private String row;

    @JsonProperty("is_available")
    private Boolean isAvailable;

    @JsonProperty("screening_id")
    private UUID screeningId;
}