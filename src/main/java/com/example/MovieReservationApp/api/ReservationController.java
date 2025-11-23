package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;

import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    // ENTITY → DTO
    private ReservationDTO toDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getScreening().getId(),
                reservation.getCreatedAt(),
                reservation.getStatus(),
                reservation.getTotalPrice()
        );
    }

    // DTO → ENTITY
    private Reservation toEntity(ReservationDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setCreatedAt(dto.getCreatedAt());
        reservation.setStatus(dto.getStatus());
        reservation.setTotalPrice(dto.getTotalPrice());

        return reservation;
    }

    // GET ALL
    @GetMapping
    public List<ReservationDTO> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ReservationDTO getById(@PathVariable UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return toDTO(reservation);
    }

    // CREATE
    @PostMapping
    public ReservationDTO create(@RequestBody ReservationDTO dto) {
        Reservation reservation = toEntity(dto);
        reservation.setId(null);

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ReservationDTO update(@PathVariable UUID id, @RequestBody ReservationDTO dto) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setCreatedAt(dto.getCreatedAt());
        reservation.setStatus(dto.getStatus());
        reservation.setTotalPrice(dto.getTotalPrice());

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        reservationRepository.deleteById(id);
    }
}
