package com.example.MovieReservationApp.application.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@JsonRootName("user")
public class UserDTO {
    private UUID id;
    private String email;
    private String fullName;
    private OffsetDateTime createdAt;
}
