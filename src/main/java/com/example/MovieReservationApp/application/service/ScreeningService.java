package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.*;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.seat.Seat;
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
public class ScreeningService {

    private final ScreeningRepository screeningRepository;

    public List<ScreeningDTO> getAllScreenings() {
        return screeningRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ScreeningDTO getScreeningById(UUID id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));
        return toDTO(screening);
    }

    public ScreeningDTO createScreening(ScreeningDTO dto) {
        Screening screening = toEntity(dto);
        screening.setId(null);
        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    public ScreeningDTO updateScreening(UUID id, ScreeningDTO dto) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));

        screening.setStartTime(dto.getStartTime());
        screening.setRoomNumber(dto.getRoomNumber());
        screening.setCapacity(dto.getCapacity());

        if (dto.getMovie() != null && dto.getMovie().getId() != null) {
            Movie movie = new Movie();
            movie.setId(dto.getMovie().getId());
            screening.setMovie(movie);
        }

        if (dto.getHall() != null && dto.getHall().getId() != null) {
            Hall hall = new Hall();
            hall.setId(dto.getHall().getId());
            screening.setHall(hall);
        }

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    public ScreeningDTO patchScreening(UUID id, ScreeningDTO dto) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));

        if (dto.getStartTime() != null) screening.setStartTime(dto.getStartTime());
        if (dto.getRoomNumber() != null) screening.setRoomNumber(dto.getRoomNumber());
        if (dto.getCapacity() != null) screening.setCapacity(dto.getCapacity());

        if (dto.getMovie() != null && dto.getMovie().getId() != null) {
            Movie movie = new Movie();
            movie.setId(dto.getMovie().getId());
            screening.setMovie(movie);
        }

        if (dto.getHall() != null && dto.getHall().getId() != null) {
            Hall hall = new Hall();
            hall.setId(dto.getHall().getId());
            screening.setHall(hall);
        }

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    public void deleteScreening(UUID id) {
        if (!screeningRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found");
        }
        screeningRepository.deleteById(id);
    }

    private ScreeningDTO toDTO(Screening screening) {
        ScreeningDTO dto = new ScreeningDTO();
        dto.setId(screening.getId());
        dto.setStartTime(screening.getStartTime());
        dto.setRoomNumber(screening.getRoomNumber());
        dto.setCapacity(screening.getCapacity());

        if (screening.getMovie() != null) {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setId(screening.getMovie().getId());
            movieDTO.setTitle(screening.getMovie().getTitle());
            movieDTO.setDescription(screening.getMovie().getDescription());
            movieDTO.setGenre(screening.getMovie().getGenre());
            movieDTO.setDuration(screening.getMovie().getDuration());
            movieDTO.setReleaseDate(screening.getMovie().getReleaseDate());
            dto.setMovie(movieDTO);
        }

        if (screening.getHall() != null) {
            HallDTO hallDTO = new HallDTO();
            hallDTO.setId(screening.getHall().getId());
            hallDTO.setName(screening.getHall().getName());
            hallDTO.setNumber(screening.getHall().getNumber());
            hallDTO.setCapacity(screening.getHall().getCapacity());
            dto.setHall(hallDTO);
        }

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

    private Screening toEntity(ScreeningDTO dto) {
        Screening screening = new Screening();
        screening.setId(dto.getId());
        screening.setStartTime(dto.getStartTime());
        screening.setRoomNumber(dto.getRoomNumber());
        screening.setCapacity(dto.getCapacity());

        if (dto.getMovie() != null && dto.getMovie().getId() != null) {
            Movie movie = new Movie();
            movie.setId(dto.getMovie().getId());
            screening.setMovie(movie);
        }

        if (dto.getHall() != null && dto.getHall().getId() != null) {
            Hall hall = new Hall();
            hall.setId(dto.getHall().getId());
            screening.setHall(hall);
        }

        return screening;
    }
}
