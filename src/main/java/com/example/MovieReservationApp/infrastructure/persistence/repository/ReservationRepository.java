package com.example.MovieReservationApp.infrastructure.persistence.repository;



import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUser_Id(UUID userId);
    List<Reservation> findByScreening_Id(UUID screeningId);
    List<Reservation> findByStatus(String status);
    List<Reservation> findByUser_IdAndStatus(UUID userId, String status);
}



