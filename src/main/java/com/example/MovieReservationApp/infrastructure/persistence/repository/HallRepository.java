package com.example.MovieReservationApp.infrastructure.persistence.repository;


import com.example.MovieReservationApp.domain.model.hall.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HallRepository extends JpaRepository<Hall, UUID> {
}
