package com.example.MovieReservationApp.domain.service;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.factory.ReservationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkflowServiceTest {

    @Mock
    private IValidationService validationService;

    @Mock
    private IComputationService computationService;

    @Mock
    private IAuditService auditService;

    @Mock
    private ReservationFactory reservationFactory;

    @InjectMocks
    private WorkflowService workflowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessReservationSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID screeningId = UUID.randomUUID();

        List<UUID> seatIds = List.of(UUID.randomUUID(), UUID.randomUUID()); // exemplu: 2 locuri

        double pricePerSeat = 25.0;
        double totalPrice = pricePerSeat * seatIds.size(); // 50.0

        Reservation mockReservation = new Reservation();
        mockReservation.setUserId(userId);
        mockReservation.setScreeningId(screeningId);
        mockReservation.setTotalPrice(totalPrice);

        when(computationService.calculateTotalPrice(pricePerSeat, seatIds.size())).thenReturn(totalPrice);
        when(reservationFactory.createReservation(userId, screeningId, seatIds, totalPrice))
                .thenReturn(mockReservation);

        Reservation result = workflowService.processReservation(userId, screeningId, seatIds, totalPrice);

        verify(validationService).validateSeats(seatIds);
        verify(auditService).logAction(contains("Rezervare creată"));
        assertEquals(totalPrice, result.getTotalPrice());
    }

}