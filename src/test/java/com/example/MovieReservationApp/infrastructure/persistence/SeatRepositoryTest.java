package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    private Screening screening1;
    private Screening screening2;

    private Seat seat1;
    private Seat seat2;
    private Seat seat3;

    @BeforeEach
    void setup() {

        screening1 = new Screening();
        screening2 = new Screening();
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