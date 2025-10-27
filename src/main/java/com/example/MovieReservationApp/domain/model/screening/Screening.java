package com.example.MovieReservationApp.domain.model.screening;

import com.example.MovieReservationApp.domain.model.seat.Seat;
import jakarta.persistence.*;
import lombok.*;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "screenings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Screening {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private OffsetDateTime startTime;

    private Integer roomNumber;

    private Integer capacity;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;
}
