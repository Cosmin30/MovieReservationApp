package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.SeatDTO;
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

    private SeatDTO toDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setId(seat.getId());
        dto.setRow(seat.getRow());
        dto.setNumber(seat.getNumber());
        dto.setIsAvailable(seat.getIsAvailable());
        if (seat.getScreening() != null) {
            dto.setScreeningId(seat.getScreening().getId());
        }
        return dto;
    }

    private Seat toEntity(SeatDTO dto) {
        Seat seat = new Seat();
        seat.setId(dto.getId());
        seat.setRow(dto.getRow());
        seat.setNumber(dto.getNumber());
        seat.setIsAvailable(dto.getIsAvailable());
        if (dto.getScreeningId() != null) {
            Screening screening = screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new RuntimeException("Screening not found"));
            seat.setScreening(screening);
        }
        return seat;
    }

    @GetMapping
    public List<SeatDTO> getAll() {
        return seatRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SeatDTO getById(@PathVariable UUID id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        return toDTO(seat);
    }

    @PostMapping
    public SeatDTO create(@RequestBody SeatDTO dto) {
        Seat seat = toEntity(dto);
        seat.setId(null);
        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    @PutMapping("/{id}")
    public SeatDTO update(@PathVariable UUID id, @RequestBody SeatDTO dto) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (dto.getScreeningId() != null) {
            Screening screening = screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new RuntimeException("Screening not found"));
            seat.setScreening(screening);
        }
        seat.setRow(dto.getRow());
        seat.setNumber(dto.getNumber());
        seat.setIsAvailable(dto.getIsAvailable());

        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    @PatchMapping("/{id}")
    public SeatDTO patch(@PathVariable UUID id, @RequestBody SeatDTO dto) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (dto.getRow() != null) seat.setRow(dto.getRow());
        if (dto.getNumber() != null) seat.setNumber(dto.getNumber());
        if (dto.getIsAvailable() != null) seat.setIsAvailable(dto.getIsAvailable());
        if (dto.getScreeningId() != null) {
            Screening screening = screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new RuntimeException("Screening not found"));
            seat.setScreening(screening);
        }

        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        seatRepository.deleteById(id);
    }
}
