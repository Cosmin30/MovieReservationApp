package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.application.dto.UserDTO;
import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;

    private ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(reservation.getUser().getId());
        dto.setUser(userDTO);

        ScreeningDTO screeningDTO = new ScreeningDTO();
        screeningDTO.setId(reservation.getScreening().getId());
        dto.setScreening(screeningDTO);

        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setStatus(reservation.getStatus());
        dto.setTotalPrice(reservation.getTotalPrice());

        return dto;
    }

    private Reservation toEntity(ReservationDTO dto) {
        User user = userRepository.findById(dto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Screening screening = screeningRepository.findById(dto.getScreening().getId())
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

    @GetMapping
    public List<ReservationDTO> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReservationDTO getById(@PathVariable UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return toDTO(reservation);
    }

    @PostMapping
    public ReservationDTO create(@RequestBody ReservationDTO dto) {
        Reservation reservation = toEntity(dto);
        reservation.setId(null);
        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }

    @PutMapping("/{id}")
    public ReservationDTO update(@PathVariable UUID id, @RequestBody ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        User user = userRepository.findById(dto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Screening screening = screeningRepository.findById(dto.getScreening().getId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setCreatedAt(dto.getCreatedAt());
        reservation.setStatus(dto.getStatus());
        reservation.setTotalPrice(dto.getTotalPrice());

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }

    @PatchMapping("/{id}")
    public ReservationDTO patch(@PathVariable UUID id, @RequestBody ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (dto.getStatus() != null) {
            reservation.setStatus(dto.getStatus());
        }
        if (dto.getCreatedAt() != null) {
            reservation.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getTotalPrice() != null) {
            reservation.setTotalPrice(dto.getTotalPrice());
        }

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }
}
