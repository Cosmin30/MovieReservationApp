package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    private User user1;
    private User user2;

    private Screening screening1;
    private Screening screening2;

    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;

    @BeforeEach
    void setup() {

        user1 = new User();
        user2 = new User();
        userRepository.saveAll(List.of(user1, user2));

        screening1 = new Screening();
        screening2 = new Screening();
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

