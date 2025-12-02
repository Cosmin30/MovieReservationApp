package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    private Movie movie1;
    private Movie movie2;
    private Movie movie3;

    @BeforeEach
    void setup() {

        movie1 = new Movie();
        movie1.setTitle("Inception");
        movie1.setDescription("Mind-bending sci-fi");
        movie1.setDuration(140);
        movie1.setGenre("SciFi");
        movie1.setReleaseDate(LocalDate.of(2010, 7, 16));

        movie2 = new Movie();
        movie2.setTitle("Interstellar");
        movie2.setDescription("Space exploration");
        movie2.setDuration(170);
        movie2.setGenre("SciFi");
        movie2.setReleaseDate(LocalDate.of(2014, 11, 7));

        movie3 = new Movie();
        movie3.setTitle("The Godfather");
        movie3.setDescription("Mafia drama");
        movie3.setDuration(175);
        movie3.setGenre("Crime");
        movie3.setReleaseDate(LocalDate.of(1972, 3, 24));

        movieRepository.saveAll(List.of(movie1, movie2, movie3));
    }

    @Test
    @DisplayName("Find movies by title containing ignore case")
    void testFindByTitleContainingIgnoreCase() {
        List<Movie> result = movieRepository.findByTitleContainingIgnoreCase("in");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Inception", "Interstellar");
    }

    @Test
    @DisplayName("Find movies by genre ignoring case")
    void testFindByGenreIgnoreCase() {
        List<Movie> result = movieRepository.findByGenreIgnoreCase("scifi");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Inception", "Interstellar");
    }

    @Test
    @DisplayName("Find movies released after date")
    void testFindByReleaseDateAfter() {
        List<Movie> result = movieRepository.findByReleaseDateAfter(LocalDate.of(2012, 1, 1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Interstellar");
    }

    @Test
    @DisplayName("Find movies released before date")
    void testFindByReleaseDateBefore() {
        List<Movie> result = movieRepository.findByReleaseDateBefore(LocalDate.of(2000, 1, 1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("The Godfather");
    }

    @Test
    @DisplayName("Find movies with duration greater than")
    void testFindByDurationGreaterThan() {
        List<Movie> result = movieRepository.findByDurationGreaterThan(160);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Interstellar", "The Godfather");
    }

    @Test
    @DisplayName("Find movies with duration less than")
    void testFindByDurationLessThan() {
        List<Movie> result = movieRepository.findByDurationLessThan(150);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
    }
}
