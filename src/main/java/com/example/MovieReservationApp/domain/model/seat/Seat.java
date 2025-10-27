package com.example.MovieReservationApp.domain.model.seat;

import jakarta.persistence.*;
import lombok.*;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id")
    private Screening screening;

    private String row;

    private Integer number;

    private Boolean isAvailable;
}