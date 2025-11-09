package com.example.MovieReservationApp.infrastructure.persistence.repository;


import com.example.MovieReservationApp.domain.model.screening.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, UUID> {

    List<Screening> findByMovieId(UUID movieId);

    List<Screening> findByRoomNumber(Integer roomNumber);

    List<Screening> findByStartTimeBetween(OffsetDateTime start, OffsetDateTime end);

    List<Screening> findByCapacityLessThan(Integer capacity);

}
