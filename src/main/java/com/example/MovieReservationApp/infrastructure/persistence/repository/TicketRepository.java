package com.example.MovieReservationApp.infrastructure.persistence.repository;

import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByReservationId(UUID reservationId);

    List<Ticket> findBySeatId(UUID seatId);

    List<Ticket> findByReservationIdAndSeatId(UUID reservationId, UUID seatId);
}
