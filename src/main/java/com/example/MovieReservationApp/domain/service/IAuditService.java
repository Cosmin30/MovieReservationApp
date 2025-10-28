package com.example.MovieReservationApp.domain.service;

public interface IAuditService {
    void logAction(String message);
}