package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    private ScreeningDTO toDTO(Screening screening) {
        ScreeningDTO dto = new ScreeningDTO();
        dto.setId(screening.getId());
        dto.setStartTime(screening.getStartTime());
        dto.setRoomNumber(screening.getRoomNumber());
        dto.setCapacity(screening.getCapacity());
        if (screening.getMovie() != null) {
            dto.setMovie(new com.example.MovieReservationApp.application.dto.MovieDTO());
            dto.getMovie().setId(screening.getMovie().getId());
        }
        if (screening.getHall() != null) {
            dto.setHall(new com.example.MovieReservationApp.application.dto.HallDTO());
            dto.getHall().setId(screening.getHall().getId());
        }
        return dto;
    }

    private Screening toEntity(ScreeningDTO dto) {
        Screening screening = new Screening();
        screening.setId(dto.getId());
        screening.setStartTime(dto.getStartTime());
        screening.setRoomNumber(dto.getRoomNumber());
        screening.setCapacity(dto.getCapacity());
        if (dto.getMovie() != null) {
            Movie movie = movieRepository.findById(dto.getMovie().getId())
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            screening.setMovie(movie);
        }
        if (dto.getHall() != null) {
            Hall hall = hallRepository.findById(dto.getHall().getId())
                    .orElseThrow(() -> new RuntimeException("Hall not found"));
            screening.setHall(hall);
        }
        return screening;
    }

    @GetMapping
    public List<ScreeningDTO> getAll() {
        return screeningRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ScreeningDTO getById(@PathVariable UUID id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        return toDTO(screening);
    }

    @PostMapping
    public ScreeningDTO create(@RequestBody ScreeningDTO dto) {
        Screening screening = toEntity(dto);
        screening.setId(null);
        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    @PutMapping("/{id}")
    public ScreeningDTO update(@PathVariable UUID id, @RequestBody ScreeningDTO dto) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        if (dto.getMovie() != null) {
            Movie movie = movieRepository.findById(dto.getMovie().getId())
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            screening.setMovie(movie);
        }

        if (dto.getHall() != null) {
            Hall hall = hallRepository.findById(dto.getHall().getId())
                    .orElseThrow(() -> new RuntimeException("Hall not found"));
            screening.setHall(hall);
        }

        screening.setStartTime(dto.getStartTime());
        screening.setRoomNumber(dto.getRoomNumber());
        screening.setCapacity(dto.getCapacity());

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    @PatchMapping("/{id}")
    public ScreeningDTO patch(@PathVariable UUID id, @RequestBody ScreeningDTO dto) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        if (dto.getStartTime() != null) screening.setStartTime(dto.getStartTime());
        if (dto.getRoomNumber() != null) screening.setRoomNumber(dto.getRoomNumber());
        if (dto.getCapacity() != null) screening.setCapacity(dto.getCapacity());

        if (dto.getMovie() != null && dto.getMovie().getId() != null) {
            Movie movie = movieRepository.findById(dto.getMovie().getId())
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            screening.setMovie(movie);
        }

        if (dto.getHall() != null && dto.getHall().getId() != null) {
            Hall hall = hallRepository.findById(dto.getHall().getId())
                    .orElseThrow(() -> new RuntimeException("Hall not found"));
            screening.setHall(hall);
        }

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        screeningRepository.deleteById(id);
    }
}
