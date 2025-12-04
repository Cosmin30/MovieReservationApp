package com.example.MovieReservationApp.infrastructure.persistence.repository;


import com.example.MovieReservationApp.domain.model.screening.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, UUID> {

    List<Screening> findByMovieId(UUID movieId);

    List<Screening> findByRoomNumber(Integer roomNumber);

    List<Screening> findByStartTimeBetween(OffsetDateTime start, OffsetDateTime end);

    List<Screening> findByCapacityLessThan(Integer capacity);

    @Query("SELECT s FROM Screening s LEFT JOIN FETCH s.seats LEFT JOIN FETCH s.movie LEFT JOIN FETCH s.hall WHERE s.id = :id")
    Optional<Screening> findByIdWithSeats(@Param("id") UUID id);

}
