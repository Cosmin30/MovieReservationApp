package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.application.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public List<SeatDTO> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/{id}")
    public SeatDTO getSeatById(@PathVariable UUID id) {
        return seatService.getSeatById(id);
    }

    @GetMapping("/screening/{screeningId}")
    public List<SeatDTO> getSeatsByScreening(@PathVariable UUID screeningId) {
        return seatService.getSeatsByScreening(screeningId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeatDTO createSeat(@RequestBody SeatDTO dto) {
        return seatService.createSeat(dto);
    }

    @PutMapping("/{id}")
    public SeatDTO updateSeat(@PathVariable UUID id, @RequestBody SeatDTO dto) {
        return seatService.updateSeat(id, dto);
    }

    @PatchMapping("/{id}")
    public SeatDTO patchSeat(@PathVariable UUID id, @RequestBody SeatDTO dto) {
        return seatService.patchSeat(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeat(@PathVariable UUID id) {
        seatService.deleteSeat(id);
    }
}
