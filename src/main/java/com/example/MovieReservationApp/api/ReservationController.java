package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ReservationDTO;
import com.example.MovieReservationApp.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDTO createReservation(@RequestParam UUID userId,
                                            @RequestParam UUID screeningId,
                                            @RequestParam List<UUID> seatIds,
                                            @RequestParam BigDecimal pricePerSeat) {
        return reservationService.createReservation(userId, screeningId, seatIds, pricePerSeat);
    }

    // -------------------------------
    // GET ALL (optional)
    // -------------------------------
    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ReservationDTO getReservationById(@PathVariable UUID id) {
        return reservationService.getReservationById(id);
    }

    // -------------------------------
    // GET BY USER
    // -------------------------------
    @GetMapping("/user/{userId}")
    public List<ReservationDTO> getReservationsByUser(@PathVariable UUID userId) {
        return reservationService.getReservationsByUser(userId);
    }

    // -------------------------------
    // UPDATE COMPLET
    // -------------------------------
    @PutMapping("/{id}")
    public Map<String, Object> updateReservation(@PathVariable UUID id,
                                                 @RequestBody ReservationDTO dto) {
        try {
            ReservationDTO updated = reservationService.updateReservation(id, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("id", updated.getId());
            response.put("status", updated.getStatus());
            response.put("totalPrice", updated.getTotalPrice());
            response.put("user", updated.getUser());
            response.put("screening", updated.getScreening());
            response.put("tickets", updated.getTickets());

            return response;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found", e);
        }
    }


    // -------------------------------
    // PATCH (UPDATE PARȚIAL)
    // -------------------------------
    @PatchMapping("/{id}")
    public ReservationDTO patchReservation(@PathVariable UUID id,
                                           @RequestBody ReservationDTO dto) {
        try {
            return reservationService.patchReservation(id, dto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found", e);
        }
    }
    // -------------------------------
    // DELETE
    // -------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable UUID id) {
        reservationService.deleteReservation(id);
    }
}
