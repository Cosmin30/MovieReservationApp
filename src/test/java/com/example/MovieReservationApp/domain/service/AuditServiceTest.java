package com.example.MovieReservationApp.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class AuditServiceTest {

    private AuditServiceImpl auditService;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        auditService = new AuditServiceImpl();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void shouldLogMessageToConsole() {
        auditService.logAction("Test log");
        String consoleOutput = outputStreamCaptor.toString().trim();
        assertTrue(consoleOutput.contains("[AUDIT] Test log"));
    }
}