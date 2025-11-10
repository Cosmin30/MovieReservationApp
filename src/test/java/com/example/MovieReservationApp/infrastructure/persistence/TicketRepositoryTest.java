package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Reservation reservation1;
    private Reservation reservation2;

    private Seat seat1;
    private Seat seat2;
    private Seat seat3;

    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;

    @BeforeEach
    void setup() {

        reservation1 = new Reservation();
        reservation2 = new Reservation();
        reservationRepository.saveAll(List.of(reservation1, reservation2));

        seat1 = new Seat();
        seat2 = new Seat();
        seat3 = new Seat();
        seatRepository.saveAll(List.of(seat1, seat2, seat3));

        ticket1 = Ticket.builder()
                .reservation(reservation1)
                .seat(seat1)
                .price(new BigDecimal("10.50"))
                .build();

        ticket2 = Ticket.builder()
                .reservation(reservation1)
                .seat(seat2)
                .price(new BigDecimal("12.00"))
                .build();

        ticket3 = Ticket.builder()
                .reservation(reservation2)
                .seat(seat3)
                .price(new BigDecimal("8.00"))
                .build();

        ticketRepository.saveAll(List.of(ticket1, ticket2, ticket3));
    }

    @Test
    @DisplayName("Find tickets by reservation ID")
    void testFindByReservationId() {
        List<Ticket> result = ticketRepository.findByReservationId(reservation1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ticket -> ticket.getSeat().getId())
                .containsExactlyInAnyOrder(seat1.getId(), seat2.getId());
    }

    @Test
    @DisplayName("Find tickets by seat ID")
    void testFindBySeatId() {
        List<Ticket> result = ticketRepository.findBySeatId(seat3.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReservation().getId())
                .isEqualTo(reservation2.getId());
    }

    @Test
    @DisplayName("Find tickets by reservation ID and seat ID")
    void testFindByReservationIdAndSeatId() {
        List<Ticket> result = ticketRepository.findByReservationIdAndSeatId(
                reservation1.getId(),
                seat1.getId()
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isEqualTo(new BigDecimal("10.50"));
    }
}
