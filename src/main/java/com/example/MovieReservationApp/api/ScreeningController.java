package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.api.dto.ScreeningDTO;
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

    // ENTITY → DTO
    private ScreeningDTO toDTO(Screening screening) {
        return new ScreeningDTO(
                screening.getId(),
                screening.getMovie().getId(),
                screening.getHall().getId(),
                screening.getStartTime(),
                screening.getRoomNumber(),
                screening.getCapacity()
        );
    }

    // DTO → ENTITY
    private Screening toEntity(ScreeningDTO dto) {

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Hall hall = hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        Screening screening = new Screening();
        screening.setId(dto.getId());
        screening.setMovie(movie);
        screening.setHall(hall);
        screening.setStartTime(dto.getStartTime());
        screening.setRoomNumber(dto.getRoomNumber());
        screening.setCapacity(dto.getCapacity());

        return screening;
    }

    // GET ALL
    @GetMapping
    public List<ScreeningDTO> getAll() {
        return screeningRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ScreeningDTO getById(@PathVariable UUID id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        return toDTO(screening);
    }

    // CREATE
    @PostMapping
    public ScreeningDTO create(@RequestBody ScreeningDTO dto) {
        Screening screening = toEntity(dto);
        screening.setId(null);
        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ScreeningDTO update(@PathVariable UUID id, @RequestBody ScreeningDTO dto) {

        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Hall hall = hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        screening.setMovie(movie);
        screening.setHall(hall);
        screening.setStartTime(dto.getStartTime());
        screening.setRoomNumber(dto.getRoomNumber());
        screening.setCapacity(dto.getCapacity());

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        screeningRepository.deleteById(id);
    }
}
