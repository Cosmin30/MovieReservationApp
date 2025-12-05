package com.example.MovieReservationApp.domain.model.seat;

import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "Screening cannot be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id")
    @JsonBackReference(value = "screening-seats")
    private Screening screening;

    @NotBlank(message = "Row cannot be blank")
    @Column(name = "\"row\"")
    private String row;

    @NotNull(message = "Seat number cannot be null")
    @Min(value = 1, message = "Seat number must be >= 1")
    private Integer number;

    @NotNull(message = "Availability must be specified")
    private Boolean isAvailable;
}
