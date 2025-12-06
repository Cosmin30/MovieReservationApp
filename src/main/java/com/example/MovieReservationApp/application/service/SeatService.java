package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreeningRepository screeningRepository;

    public List<SeatDTO> getAllSeats() {
        return seatRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SeatDTO getSeatById(UUID id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        return toDTO(seat);
    }

    public List<SeatDTO> getSeatsByScreening(UUID screeningId) {
        return seatRepository.findByScreeningId(screeningId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SeatDTO> getAvailableSeatsByScreening(UUID screeningId) {
        return seatRepository.findByScreeningIdAndIsAvailable(screeningId, true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SeatDTO createSeat(SeatDTO dto) {
        Seat seat = toEntity(dto);
        seat.setId(null); // ID generat automat
        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    public SeatDTO updateSeat(UUID id, SeatDTO dto) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        seat.setNumber(dto.getNumber());
        seat.setRow(dto.getRow());
        seat.setIsAvailable(dto.getIsAvailable());

        if (dto.getScreeningId() != null) {
            seat.setScreening(screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found")));
        }

        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    public SeatDTO patchSeat(UUID id, SeatDTO dto) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        if (dto.getNumber() != null) seat.setNumber(dto.getNumber());
        if (dto.getRow() != null) seat.setRow(dto.getRow());
        if (dto.getIsAvailable() != null) seat.setIsAvailable(dto.getIsAvailable());

        if (dto.getScreeningId() != null) {
            seat.setScreening(screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found")));
        }

        seat = seatRepository.save(seat);
        return toDTO(seat);
    }

    public void deleteSeat(UUID id) {
        if (!seatRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found");
        }
        seatRepository.deleteById(id);
    }

    private SeatDTO toDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setId(seat.getId());
        dto.setNumber(seat.getNumber());
        dto.setRow(seat.getRow());
        dto.setIsAvailable(seat.getIsAvailable());
        dto.setScreeningId(seat.getScreening() != null ? seat.getScreening().getId() : null);
        return dto;
    }

    private Seat toEntity(SeatDTO dto) {
        Seat seat = new Seat();
        seat.setId(dto.getId());
        seat.setNumber(dto.getNumber());
        seat.setRow(dto.getRow());
        seat.setIsAvailable(dto.getIsAvailable());

        if (dto.getScreeningId() != null) {
            seat.setScreening(screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found")));
        }

        return seat;
    }
}
