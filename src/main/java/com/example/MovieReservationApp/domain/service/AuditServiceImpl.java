package com.example.MovieReservationApp.domain.service;
import com.example.MovieReservationApp.domain.service.IAuditService;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements IAuditService {

    @Override
    public void logAction(String message) {
        System.out.println("[AUDIT] " + message);
    }
}