package com.example.MovieReservationApp.infrastructure.persistence.repository;

import com.example.MovieReservationApp.domain.model.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByGenreIgnoreCase(String genre);
    List<Movie> findByReleaseDateAfter(LocalDate date);
    List<Movie> findByReleaseDateBefore(LocalDate date);
    List<Movie> findByDurationGreaterThan(Integer duration);
    List<Movie> findByDurationLessThan(Integer duration);

}
