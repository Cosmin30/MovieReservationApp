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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
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
    public ReservationDTO create(@RequestBody Map<String, Object> dtoMap) {
        UUID userId = UUID.fromString(((Map<String, String>) dtoMap.get("user")).get("id"));
        UUID screeningId = UUID.fromString(((Map<String, String>) dtoMap.get("screening")).get("id"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setStatus((String) dtoMap.get("status"));
        reservation.setTotalPrice(new BigDecimal(dtoMap.get("totalPrice").toString()));
        reservation.setCreatedAt(OffsetDateTime.parse((String) dtoMap.get("createdAt")));

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }

    @PutMapping("/{id}")
    public ReservationDTO update(@PathVariable UUID id, @RequestBody Map<String, Object> dtoMap) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        UUID userId = UUID.fromString(((Map<String, String>) dtoMap.get("user")).get("id"));
        UUID screeningId = UUID.fromString(((Map<String, String>) dtoMap.get("screening")).get("id"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setStatus((String) dtoMap.get("status"));
        reservation.setTotalPrice(new BigDecimal(dtoMap.get("totalPrice").toString()));
        reservation.setCreatedAt(OffsetDateTime.parse((String) dtoMap.get("createdAt")));

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }
    @PatchMapping("/{id}")
    public ReservationDTO patch(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (updates.containsKey("status")) {
            reservation.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("totalPrice")) {
            reservation.setTotalPrice(new BigDecimal(updates.get("totalPrice").toString()));
        }
        if (updates.containsKey("createdAt")) {
            reservation.setCreatedAt(OffsetDateTime.parse((String) updates.get("createdAt")));
        }

        reservation = reservationRepository.save(reservation);
        return toDTO(reservation);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }
        reservationRepository.deleteById(id);
    }
}
