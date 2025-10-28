package com.example.MovieReservationApp.domain.service;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.factory.ReservationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


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

        Reservation mockReservation = new Reservation();
        mockReservation.setUserId(userId);
        mockReservation.setScreeningId(screeningId);
        mockReservation.setTotalPrice(50.0);

        when(computationService.calculateTotalPrice(25.0, 2)).thenReturn(50.0);
        when(reservationFactory.createReservation(userId, screeningId, 50.0)).thenReturn(mockReservation);

        Reservation result = workflowService.processReservation(userId, screeningId, 25.0, 2, "user@example.com");

        verify(validationService).validateEmail("user@example.com");
        verify(auditService).logAction(contains("Rezervare creată"));
        assertEquals(50.0, result.getTotalPrice());
    }
}