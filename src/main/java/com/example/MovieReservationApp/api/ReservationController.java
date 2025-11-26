package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDTO createReservation(@RequestParam UUID userId,
                                            @RequestParam UUID screeningId,
                                            @RequestParam List<UUID> seatIds,
                                            @RequestParam BigDecimal pricePerSeat) {
        return reservationService.createReservation(userId, screeningId, seatIds, pricePerSeat);
    }

    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ReservationDTO getReservationById(@PathVariable UUID id) {
        return reservationService.getReservationById(id);
    }

    @GetMapping("/user/{userId}")
    public List<ReservationDTO> getReservationsByUser(@PathVariable UUID userId) {
        return reservationService.getReservationsByUser(userId);
    }

    @PutMapping("/{id}")
    public ReservationDTO updateReservation(@PathVariable UUID id,
                                            @RequestBody ReservationDTO dto) {
        return reservationService.updateReservation(id, dto);
    }

    @PatchMapping("/{id}")
    public ReservationDTO patchReservation(@PathVariable UUID id,
                                           @RequestBody ReservationDTO dto) {
        return reservationService.patchReservation(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable UUID id) {
        reservationService.deleteReservation(id);
    }
}