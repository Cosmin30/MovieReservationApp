package com.example.MovieReservationApp.infrastructure.persistence.repository;

import com.example.MovieReservationApp.domain.model.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {

    List<Seat> findByScreeningId(UUID screeningId);

    List<Seat> findByRow(String row);

    List<Seat> findByScreeningIdAndIsAvailable(UUID screeningId, Boolean isAvailable);

    List<Seat> findByScreeningIdAndRow(UUID screeningId, String row);
}
