package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.api.dto.SeatDTO;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.screening.Screening;

import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    // ENTITY → DTO
    private SeatDTO toDTO(Seat seat) {
        return new SeatDTO(
                seat.getId(),
                seat.getScreening().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getIsAvailable()
        );
    }

    // DTO → ENTITY
    private Seat toEntity(SeatDTO dto) {

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        Seat seat = new Seat();
        seat.setId(dto.getId());
        seat.setScreening(screening);
        seat.setRow(dto.getRow());
        seat.setNumber(dto.getNumber());
        seat.setIsAvailable(dto.getIsAvailable());

        return seat;
    }

    // GET ALL
    @GetMapping
    public List<SeatDTO> getAll() {
        return seatRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public SeatDTO getById(@PathVariable UUID id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        return toDTO(seat);
    }

    // CREATE
    @PostMapping
    public SeatDTO create(@RequestBody SeatDTO dto) {
        Seat seat = toEntity(dto);
        seat.setId(null);

        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    // UPDATE
    @PutMapping("/{id}")
    public SeatDTO update(@PathVariable UUID id, @RequestBody SeatDTO dto) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        seat.setScreening(screening);
        seat.setRow(dto.getRow());
        seat.setNumber(dto.getNumber());
        seat.setIsAvailable(dto.getIsAvailable());

        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        seatRepository.deleteById(id);
    }
}
