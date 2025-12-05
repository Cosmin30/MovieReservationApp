package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    private Reservation reservation1;
    private Reservation reservation2;

    private Seat seat1;
    private Seat seat2;
    private Seat seat3;

    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;
    private Hall hall1;
    @BeforeEach
    void setup() {
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        reservationRepository.deleteAll();
        screeningRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
        hallRepository.deleteAll();

        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setDuration(120);
        movie.setGenre("Action");
        movie.setReleaseDate(LocalDate.of(2024, 1, 1));
        movie = movieRepository.save(movie);

        Hall hall1= new Hall();
        hall1.setName("Sala Talentului");
        hall1.setCapacity(140);
        hall1.setNumber(6000 + (int)(Math.random() * 1000)); // Unique number
        hallRepository.save(hall1);

        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setFullName("John Doe");
        user.setCreatedAt(OffsetDateTime.now());
        user = userRepository.save(user);

        Screening screening = new Screening();
        screening.setMovie(movie);
        screening.setRoomNumber(1);
        screening.setStartTime(OffsetDateTime.now().plusDays(1));
        screening.setCapacity(100);
        screening.setHall(hall1);
        screening = screeningRepository.save(screening);

        reservation1 = new Reservation();
        reservation1.setUser(user);
        reservation1.setScreening(screening);
        reservation1.setStatus("CONFIRMED");
        reservation1.setTotalPrice(BigDecimal.valueOf(22.50));
        reservation1.setCreatedAt(OffsetDateTime.now());

        reservation2 = new Reservation();
        reservation2.setUser(user);
        reservation2.setScreening(screening);
        reservation2.setStatus("CONFIRMED");
        reservation2.setTotalPrice(BigDecimal.valueOf(8.00));
        reservation2.setCreatedAt(OffsetDateTime.now());

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        seat1 = new Seat();
        seat1.setScreening(screening);
        seat1.setRow("A");
        seat1.setNumber(1);
        seat1.setIsAvailable(false);

        seat2 = new Seat();
        seat2.setScreening(screening);
        seat2.setRow("A");
        seat2.setNumber(2);
        seat2.setIsAvailable(false);

        seat3 = new Seat();
        seat3.setScreening(screening);
        seat3.setRow("B");
        seat3.setNumber(1);
        seat3.setIsAvailable(false);

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
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("10.50"));
    }
}