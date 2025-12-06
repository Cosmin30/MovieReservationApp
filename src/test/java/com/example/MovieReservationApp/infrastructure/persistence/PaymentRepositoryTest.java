package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
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
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    private Reservation reservation1;
    private Reservation reservation2;

    private Payment payment1;
    private Payment payment2;
    private Payment payment3;

    @BeforeEach
    void setup() {
        // --- Clear tables ---
        paymentRepository.deleteAll();
        reservationRepository.deleteAll();
        screeningRepository.deleteAll();
        hallRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        // --- Movie ---
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setDuration(120);
        movie.setGenre("Action");
        movie.setReleaseDate(LocalDate.of(2024, 1, 1));
        movie = movieRepository.save(movie);

        // --- User ---
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword123");
        user.setFullName("John Doe");
        user.setCreatedAt(OffsetDateTime.now());
        user = userRepository.save(user);

        // --- Halls ---
        Hall hall1 = new Hall();
        hall1.setName("Hall 1");
        hall1.setCapacity(100);
        hall1.setNumber(1);

        hall1 = hallRepository.save(hall1);

        Hall hall2 = new Hall();
        hall2.setName("Hall 2");
        hall2.setCapacity(80);
        hall2.setNumber(2); //

        hall2 = hallRepository.save(hall2);

        // --- Screenings ---
        Screening screening1 = new Screening();
        screening1.setMovie(movie);
        screening1.setRoomNumber(1);
        screening1.setStartTime(OffsetDateTime.now().plusDays(1));
        screening1.setCapacity(100);
        screening1.setHall(hall1);
        screening1 = screeningRepository.save(screening1);

        Screening screening2 = new Screening();
        screening2.setMovie(movie);
        screening2.setRoomNumber(2);
        screening2.setStartTime(OffsetDateTime.now().plusDays(2));
        screening2.setCapacity(80);
        screening2.setHall(hall2);
        screening2 = screeningRepository.save(screening2);

        // --- Reservations ---
        reservation1 = new Reservation();
        reservation1.setUser(user);
        reservation1.setScreening(screening1);
        reservation1.setStatus("CREATED");
        reservation1.setTotalPrice(BigDecimal.valueOf(30.00));
        reservation1.setCreatedAt(OffsetDateTime.now());

        reservation2 = new Reservation();
        reservation2.setUser(user);
        reservation2.setScreening(screening2);
        reservation2.setStatus("CREATED");
        reservation2.setTotalPrice(BigDecimal.valueOf(5.00));
        reservation2.setCreatedAt(OffsetDateTime.now());

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        // --- Payments ---
        payment1 = Payment.builder()
                .reservation(reservation1)
                .status("PAID")
                .paidAt(OffsetDateTime.now())
                .amount(new BigDecimal("10.00"))
                .build();

        payment2 = Payment.builder()
                .reservation(reservation1)
                .status("PENDING")
                .paidAt(null)
                .amount(new BigDecimal("20.00"))
                .build();

        payment3 = Payment.builder()
                .reservation(reservation2)
                .status("FAILED")
                .paidAt(null)
                .amount(new BigDecimal("5.00"))
                .build();

        paymentRepository.saveAll(List.of(payment1, payment2, payment3));
    }

    @Test
    @DisplayName("Find payments by reservation")
    void testFindByReservation() {
        List<Payment> result = paymentRepository.findByReservation(reservation1);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Payment::getStatus)
                .containsExactlyInAnyOrder("PAID", "PENDING");
    }

    @Test
    @DisplayName("Find payments by status ignoring case")
    void testFindByStatusIgnoreCase() {
        List<Payment> result = paymentRepository.findByStatusIgnoreCase("paid");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Find payments with amount greater than")
    void testFindByAmountGreaterThan() {
        List<Payment> result = paymentRepository.findByAmountGreaterThan(new BigDecimal("10.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Find payments with amount less than")
    void testFindByAmountLessThan() {
        List<Payment> result = paymentRepository.findByAmountLessThan(new BigDecimal("10.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("FAILED");
    }
}
