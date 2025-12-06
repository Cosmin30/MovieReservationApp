package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private HallRepository hallRepository;

    private Screening screening1;
    private Screening screening2;

    private Seat seat1;
    private Seat seat2;
    private Seat seat3;

    @BeforeEach
    void setup() {
        seatRepository.deleteAll();
        screeningRepository.deleteAll();
        movieRepository.deleteAll();
        hallRepository.deleteAll();

        Hall hall= new Hall();
        hall.setName("Sala Fericirii");
        hall.setNumber(5000 + (int)(Math.random() * 1000)); // Unique number
        hall.setCapacity(120);
        hallRepository.save(hall);

        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setDuration(120);
        movie.setGenre("Action");
        movie.setReleaseDate(LocalDate.of(2024, 1, 1));
        movie = movieRepository.save(movie);

        screening1 = new Screening();
        screening1.setMovie(movie);
        screening1.setRoomNumber(5);
        screening1.setStartTime(OffsetDateTime.now().plusDays(1));
        screening1.setHall(hall);
        screening1.setCapacity(100);

        screening2 = new Screening();
        screening2.setMovie(movie);
        screening2.setStartTime(OffsetDateTime.now().plusDays(2));
        screening2.setCapacity(80);
        screening2.setRoomNumber(3);
        screening2.setHall(hall);

        screeningRepository.saveAll(List.of(screening1, screening2));

        seat1 = new Seat();
        seat1.setScreening(screening1);
        seat1.setRow("A");
        seat1.setNumber(1);
        seat1.setIsAvailable(true);

        seat2 = new Seat();
        seat2.setScreening(screening1);
        seat2.setRow("A");
        seat2.setNumber(2);
        seat2.setIsAvailable(false);

        seat3 = new Seat();
        seat3.setScreening(screening2);
        seat3.setRow("B");
        seat3.setNumber(1);
        seat3.setIsAvailable(true);

        seatRepository.saveAll(List.of(seat1, seat2, seat3));
    }

    @Test
    @DisplayName("Find seats by screening ID")
    void testFindByScreeningId() {
        List<Seat> result = seatRepository.findByScreeningId(screening1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Seat::getNumber)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("Find seats by row")
    void testFindByRow() {
        List<Seat> result = seatRepository.findByRow("A");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Seat::getNumber)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("Find available seats by screening ID")
    void testFindByScreeningIdAndIsAvailable() {
        List<Seat> result = seatRepository.findByScreeningIdAndIsAvailable(screening1.getId(), true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRow()).isEqualTo("A");
        assertThat(result.get(0).getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("Find seats by screening ID and row")
    void testFindByScreeningIdAndRow() {
        List<Seat> result = seatRepository.findByScreeningIdAndRow(screening1.getId(), "A");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Seat::getNumber)
                .containsExactlyInAnyOrder(1, 2);
    }
}