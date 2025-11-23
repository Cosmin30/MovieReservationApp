package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.application.dto.SeatDTO;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;

    public List<ScreeningDTO> getAllScreenings() {
        return screeningRepository.findAll().stream().map(screening -> mapToDTO(screening))
                .collect(Collectors.toList());
    }

    public ScreeningDTO getScreeningById(UUID id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        return mapToDTO(screening);
    }

    private ScreeningDTO mapToDTO(Screening screening) {
        ScreeningDTO dto = new ScreeningDTO();
        dto.setId(screening.getId());

        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(screening.getMovie().getId());
        movieDTO.setTitle(screening.getMovie().getTitle());
        movieDTO.setDescription(screening.getMovie().getDescription());
        movieDTO.setGenre(screening.getMovie().getGenre());
        movieDTO.setDuration(screening.getMovie().getDuration());
        movieDTO.setReleaseDate(screening.getMovie().getReleaseDate());
        dto.setMovie(movieDTO);

        HallDTO hallDTO = new HallDTO();
        hallDTO.setId(screening.getHall().getId());
        hallDTO.setName(screening.getHall().getName());
        hallDTO.setNumber(screening.getHall().getNumber());
        hallDTO.setCapacity(screening.getHall().getCapacity());
        dto.setHall(hallDTO);

        dto.setStartTime(screening.getStartTime());
        dto.setRoomNumber(screening.getRoomNumber());
        dto.setCapacity(screening.getCapacity());

        List<SeatDTO> seats = screening.getSeats().stream().map(seat -> {
            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setId(seat.getId());
            seatDTO.setNumber(seat.getNumber());
            seatDTO.setRow(seat.getRow());
            seatDTO.setIsAvailable(seat.getIsAvailable());
            seatDTO.setScreeningId(screening.getId());
            return seatDTO;
        }).collect(Collectors.toList());
        dto.setSeats(seats);

        return dto;
    }
}
