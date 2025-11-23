package com.example.MovieReservationApp.domain.model.hall;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "halls",
        uniqueConstraints = {@UniqueConstraint(columnNames = "number")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hall {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "Number cannot be null")
    @Min(value = 1, message = "Number must be greater than 0")
    @Column(nullable = false, unique = true)
    private Integer number;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be greater than 0")
    @Column(nullable = false)
    private Integer capacity;

    @NotBlank(message = "Name cannot be blank")
    @Column(length = 255)
    private String name;
}
