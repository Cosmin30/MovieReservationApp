package com.example.MovieReservationApp.domain.model.screening;

import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "screenings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screening {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "Movie cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    @JsonBackReference(value = "movie-screenings")
    private Movie movie;

    @NotNull(message = "Start time cannot be null")
    private OffsetDateTime startTime;

    @NotNull(message = "Room number cannot be null")
    @Min(value = 1, message = "Room number must be >= 1")
    private Integer roomNumber;

    @NotNull(message = "Hall cannot be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "screening-seats")
    private List<Seat> seats;
}
