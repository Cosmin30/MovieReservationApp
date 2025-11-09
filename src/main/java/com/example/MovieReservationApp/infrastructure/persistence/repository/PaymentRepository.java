package com.example.MovieReservationApp.infrastructure.persistence.repository;

import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByReservation(Reservation reservation);
    List<Payment> findByStatusIgnoreCase(String status);
    List<Payment> findByAmountGreaterThan(java.math.BigDecimal amount);
    List<Payment> findByAmountLessThan(java.math.BigDecimal amount);
}
