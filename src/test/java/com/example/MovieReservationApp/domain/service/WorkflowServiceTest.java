package com.example.MovieReservationApp.domain.service;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.infrastructure.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ScreeningRepository screeningRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private IValidationService validationService;
    @Mock private IComputationService computationService;
    @Mock private IAuditService auditService;

    @InjectMocks
    private WorkflowService workflowService;

    @Captor
    private ArgumentCaptor<Reservation> reservationCaptor;

    private UUID userId;
    private UUID screeningId;
    private List<UUID> seatIds;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        screeningId = UUID.randomUUID();
        seatIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    void shouldProcessReservationSuccessfully() {

        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Screening mockScreening = new Screening();
        mockScreening.setId(screeningId);
        when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(mockScreening));

        Seat seat1 = new Seat();
        seat1.setId(seatIds.get(0));
        seat1.setIsAvailable(true);

        Seat seat2 = new Seat();
        seat2.setId(seatIds.get(1));
        seat2.setIsAvailable(true);

        List<Seat> seats = List.of(seat1, seat2);
        when(seatRepository.findAllById(seatIds)).thenReturn(seats);

        double ticketPrice = 25.0;
        double totalPrice = ticketPrice * seats.size();
        when(computationService.calculateTotalPrice(eq(ticketPrice), eq(seats.size()))).thenReturn(totalPrice);

        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(seatRepository.save(any(Seat.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Reservation reservation = workflowService.processReservation(userId, screeningId, seatIds, ticketPrice);


        assertThat(reservation).isNotNull();
        assertThat(reservation.getUser()).isEqualTo(mockUser);
        assertThat(reservation.getScreening()).isEqualTo(mockScreening);

        assertThat(reservation.getTotalPrice())
                .isEqualByComparingTo(BigDecimal.valueOf(totalPrice));
        assertThat(reservation.getStatus()).isEqualTo("CREATED");
        assertThat(reservation.getCreatedAt()).isNotNull();

        verify(validationService, times(2)).validateSeatAvailability(true);
        verify(reservationRepository).save(any(Reservation.class));
        verify(ticketRepository, times(2)).save(any(Ticket.class));
        verify(seatRepository, times(2)).save(any(Seat.class));
        verify(auditService).logAction(contains("Rezervare creată"));
    }
}