package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
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
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    private User user1;
    private User user2;

    private Screening screening1;
    private Screening screening2;

    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
        screeningRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setDuration(120);
        movie.setGenre("Action");
        movie.setReleaseDate(LocalDate.of(2024, 1, 1));
        movie = movieRepository.save(movie);

        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPasswordHash("hashedPassword123");
        user1.setFullName("John Doe");
        user1.setCreatedAt(OffsetDateTime.now());

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("hashedPassword456");
        user2.setFullName("Jane Smith");
        user2.setCreatedAt(OffsetDateTime.now());

        userRepository.saveAll(List.of(user1, user2));

        screening1 = new Screening();
        screening1.setMovie(movie);
        screening1.setRoomNumber(1);
        screening1.setStartTime(OffsetDateTime.now().plusDays(1));
        screening1.setCapacity(100);

        screening2 = new Screening();
        screening2.setMovie(movie);
        screening2.setRoomNumber(2);
        screening2.setStartTime(OffsetDateTime.now().plusDays(2));
        screening2.setCapacity(80);

        screeningRepository.saveAll(List.of(screening1, screening2));

        reservation1 = new Reservation();
        reservation1.setUser(user1);
        reservation1.setScreening(screening1);
        reservation1.setCreatedAt(OffsetDateTime.now());
        reservation1.setStatus("CONFIRMED");
        reservation1.setTotalPrice(15.00);

        reservation2 = new Reservation();
        reservation2.setUser(user1);
        reservation2.setScreening(screening2);
        reservation2.setCreatedAt(OffsetDateTime.now());
        reservation2.setStatus("CANCELLED");
        reservation2.setTotalPrice(0.00);

        reservation3 = new Reservation();
        reservation3.setUser(user2);
        reservation3.setScreening(screening1);
        reservation3.setCreatedAt(OffsetDateTime.now());
        reservation3.setStatus("CONFIRMED");
        reservation3.setTotalPrice(12.00);

        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));
    }

    @Test
    @DisplayName("Find reservations by User ID")
    void testFindByUserId() {
        var result = reservationRepository.findByUser_Id(user1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Reservation::getStatus)
                .containsExactlyInAnyOrder("CONFIRMED", "CANCELLED");
    }

    @Test
    @DisplayName("Find reservations by Screening ID")
    void testFindByScreeningId() {
        var result = reservationRepository.findByScreening_Id(screening1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Reservation::getUser)
                .containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    @DisplayName("Find reservations by Status")
    void testFindByStatus() {
        var result = reservationRepository.findByStatus("CONFIRMED");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Reservation::getUser)
                .containsExactlyInAnyOrder(user1, user2);
    }
}