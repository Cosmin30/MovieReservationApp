package com.example.MovieReservationApp.domain.model.movie;

import com.example.MovieReservationApp.domain.model.screening.Screening;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @NotNull(message = "Duration cannot be null")
    private Integer duration;

    @NotBlank(message = "Genre cannot be blank")
    private String genre;

    private LocalDate releaseDate;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "movie-screenings")
    private List<Screening> screenings;
}
