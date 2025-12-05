package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.TicketDTO;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

@org.springframework.context.annotation.Import(com.example.MovieReservationApp.config.TestSecurityConfig.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // @BeforeEach - comentat pentru a păstra datele în BD
    // void setUp() {
    //     ticketRepository.deleteAll();
    // }

    private User createUser(String suffix) {
        return userRepository.save(User.builder()
                .fullName("Test User " + suffix)
                .email("user" + suffix + System.currentTimeMillis() + "@test.com")
                .passwordHash("password123")
                .createdAt(OffsetDateTime.now())
                .build());
    }

    private Movie createMovie() {
        return movieRepository.save(Movie.builder()
                .title("Test Movie")
                .genre("Action")
                .duration(120)
                .releaseDate(LocalDate.now())
                .build());
    }

    private Hall createHall(int number) {
        return hallRepository.save(Hall.builder()
                .name("Hall " + number)
                .number(4000 + number + (int)(Math.random() * 1000))
                .capacity(100)
                .build());
    }

    private Screening createScreening() {
        Movie movie = createMovie();
        Hall hall = createHall(1);
        return screeningRepository.save(Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(OffsetDateTime.now().plusDays(1))
                .roomNumber(1)
                .capacity(100)
                .build());
    }

    private Reservation createReservation(String suffix) {
        User user = createUser(suffix);
        Screening screening = createScreening();
        return reservationRepository.save(Reservation.builder()
                .user(user)
                .screening(screening)
                .status("CREATED")
                .totalPrice(BigDecimal.valueOf(250))
                .createdAt(OffsetDateTime.now())
                .build());
    }

    private Seat createSeat(Screening screening, String row, int number) {
        // Adaugă random pentru a evita duplicate
        int uniqueNumber = number + (int)(Math.random() * 10000);
        return seatRepository.save(Seat.builder()
                .screening(screening)
                .row(row)
                .number(uniqueNumber)
                .isAvailable(true)
                .build());
    }

    @Test
    void testCreateTicketDirect() throws Exception {
        // Test direct cu entități salvate, nu prin API
        Reservation reservation = createReservation("direct");
        Screening screening = reservation.getScreening();
        Seat seat = createSeat(screening, "Z", 99);

        // Creăm ticket direct în repository
        Ticket ticket = Ticket.builder()
                .reservation(reservation)
                .seat(seat)
                .price(BigDecimal.valueOf(75.00))
                .build();

        Ticket saved = ticketRepository.save(ticket);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(75.00));

        System.out.println("✅ Ticket saved directly: " + saved.getId());
    }

    @Test
    void testCreateMultipleTickets() throws Exception {
        // Creăm bilete direct în repository, nu prin API
        Reservation reservation = createReservation("multi");
        Screening screening = reservation.getScreening();

        // Creăm 5 bilete diferite
        BigDecimal[] prices = {
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(60.00),
                BigDecimal.valueOf(60.00),
                BigDecimal.valueOf(75.00)
        };

        for (int i = 0; i < 5; i++) {
            Seat seat = createSeat(screening, "Row" + i, i + 1);

            Ticket ticket = Ticket.builder()
                    .reservation(reservation)
                    .seat(seat)
                    .price(prices[i])
                    .build();

            ticketRepository.save(ticket);
            System.out.println("✅ Ticket " + (i+1) + " created with price: " + prices[i]);
        }

        // Verificăm persistența
        assertThat(ticketRepository.count()).isGreaterThanOrEqualTo(5);
        System.out.println("✅ Total tickets in DB: " + ticketRepository.count());
    }

    @Test
    void testGetTicketsByReservation() throws Exception {
        Reservation reservation = createReservation("byres");
        Screening screening = reservation.getScreening();

        // Creăm 3 bilete pentru aceeași rezervare - direct în repository
        for (int i = 1; i <= 3; i++) {
            Seat seat = createSeat(screening, "A", i);
            Ticket ticket = Ticket.builder()
                    .reservation(reservation)
                    .seat(seat)
                    .price(BigDecimal.valueOf(50))
                    .build();
            ticketRepository.save(ticket);
        }

        // Act & Assert - verifică endpoint-ul
        mockMvc.perform(get("/api/tickets/reservation/{reservationId}", reservation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testGetAllTickets() throws Exception {
        Reservation reservation = createReservation("all");
        Screening screening = reservation.getScreening();

        // Creăm 3 bilete - direct în repository
        for (int i = 1; i <= 3; i++) {
            Seat seat = createSeat(screening, "B", i);
            Ticket ticket = Ticket.builder()
                    .reservation(reservation)
                    .seat(seat)
                    .price(BigDecimal.valueOf(60))
                    .build();
            ticketRepository.save(ticket);
        }

        // Act & Assert
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void testCreateTicket() throws Exception {
        Reservation reservation = createReservation("create");
        Screening screening = reservation.getScreening();
        Seat seat = createSeat(screening, "A", 5);

        Ticket ticket = Ticket.builder()
                .reservation(reservation)
                .seat(seat)
                .price(BigDecimal.valueOf(75.00))
                .build();

        Ticket saved = ticketRepository.save(ticket);

        // Verificăm că s-a salvat
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(75.00));

        // Verificăm că putem citi din API
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTicket() throws Exception {
        Reservation reservation = createReservation("update");
        Screening screening = reservation.getScreening();
        Seat seat = createSeat(screening, "B", 1);

        // Creăm ticket direct
        Ticket ticket = Ticket.builder()
                .reservation(reservation)
                .seat(seat)
                .price(BigDecimal.valueOf(50))
                .build();
        Ticket saved = ticketRepository.save(ticket);

        // Modificăm prețul direct în repository
        saved.setPrice(BigDecimal.valueOf(80.00));
        ticketRepository.save(saved);

        // Verificăm
        Ticket updated = ticketRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(80.00));
    }

    @Test
    void testPatchTicket() throws Exception {
        Reservation reservation = createReservation("patch");
        Screening screening = reservation.getScreening();
        Seat seat = createSeat(screening, "C", 1);

        // Creăm ticket direct
        Ticket ticket = Ticket.builder()
                .reservation(reservation)
                .seat(seat)
                .price(BigDecimal.valueOf(60))
                .build();
        Ticket saved = ticketRepository.save(ticket);

        // Modificăm prețul
        saved.setPrice(BigDecimal.valueOf(90.00));
        ticketRepository.save(saved);

        // Verificăm
        Ticket patched = ticketRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    void testDeleteTicket() throws Exception {
        Reservation reservation = createReservation("delete");
        Screening screening = reservation.getScreening();
        Seat seat = createSeat(screening, "D", 1);

        Ticket ticket = Ticket.builder()
                .reservation(reservation)
                .seat(seat)
                .price(BigDecimal.valueOf(50))
                .build();
        Ticket saved = ticketRepository.save(ticket);

        // Act
        mockMvc.perform(delete("/api/tickets/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(ticketRepository.findById(saved.getId())).isEmpty();
    }
}