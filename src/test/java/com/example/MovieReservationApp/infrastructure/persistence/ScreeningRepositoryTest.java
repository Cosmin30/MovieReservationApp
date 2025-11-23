package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScreeningRepositoryTest {

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private MovieRepository movieRepository;
    private  Hall hall1;
    private Movie movie1;
    private Movie movie2;

    private Screening screening1;
    private Screening screening2;
    private Screening screening3;

    @BeforeEach
    void setup() {

        hall1 = new Hall(); // fără "Hall" în față
        hall1.setName("Hall 1");
        hall1.setCapacity(100);
        hall1.setNumber(1);
        hallRepository.save(hall1);

        movie1 = new Movie();
        movie1.setTitle("Inception");
        movie1.setDescription("Mind-bending");
        movie1.setGenre("Thriller");
        movie1.setDuration(150);

        movie2 = new Movie();
        movie2.setTitle("Godfather");
        movie2.setDescription("Mafia story");
        movie2.setGenre("Action");
        movie2.setDuration(180);

        movieRepository.saveAll(List.of(movie1, movie2));

        screening1 = new Screening();
        screening1.setMovie(movie1);
        screening1.setStartTime(OffsetDateTime.now().plusDays(1));
        screening1.setRoomNumber(1);
        screening1.setHall(hall1);
        screening1.setCapacity(120);

        screening2 = new Screening();
        screening2.setMovie(movie1);
        screening2.setStartTime(OffsetDateTime.now().plusDays(2));
        screening2.setRoomNumber(1);
        screening2.setHall(hall1);

        screening2.setCapacity(80);

        screening3 = new Screening();
        screening3.setMovie(movie2);
        screening3.setStartTime(OffsetDateTime.now().plusDays(3));
        screening3.setRoomNumber(2);
        screening3.setCapacity(60);
        screening3.setHall(hall1);

        screeningRepository.saveAll(List.of(screening1, screening2, screening3));
    }

    @Test
    @DisplayName("Find screenings by movie id")
    void testFindByMovieId() {
        List<Screening> result = screeningRepository.findByMovieId(movie1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Screening::getRoomNumber)
                .containsExactlyInAnyOrder(1, 1);
    }

    @Test
    @DisplayName("Find screenings by room number")
    void testFindByRoomNumber() {
        List<Screening> result = screeningRepository.findByRoomNumber(1);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Screening::getMovie)
                .containsExactlyInAnyOrder(movie1, movie1);
    }

    @Test
    @DisplayName("Find screenings by start time range")
    void testFindByStartTimeBetween() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.plusHours(1);
        OffsetDateTime end   = now.plusDays(2).plusHours(12);

        List<Screening> result = screeningRepository.findByStartTimeBetween(start, end);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Screening::getMovie)
                .containsExactlyInAnyOrder(movie1, movie1);
    }

    @Test
    @DisplayName("Find screenings with capacity less than")
    void testFindByCapacityLessThan() {
        List<Screening> result = screeningRepository.findByCapacityLessThan(100);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Screening::getCapacity)
                .containsExactlyInAnyOrder(80, 60);
    }
}
