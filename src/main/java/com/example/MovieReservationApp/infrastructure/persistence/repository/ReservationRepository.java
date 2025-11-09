package com.example.MovieReservationApp.infrastructure.persistence.repository;



import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    java.util.List<Reservation> findByUserId(UUID userId);
    java.util.List<Reservation> findByScreeningId(UUID screeningId);
    java.util.List<Reservation> findByStatus(String status);
    java.util.List<Reservation> findByUserIdAndStatus(UUID userId, String status);
}


