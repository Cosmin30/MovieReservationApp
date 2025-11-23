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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
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
    public ScreeningDTO create(@RequestBody Map<String, Object> dtoMap) {
        UUID movieId = UUID.fromString(((Map<String, String>) dtoMap.get("movie")).get("id"));
        UUID hallId = UUID.fromString(((Map<String, String>) dtoMap.get("hall")).get("id"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        Screening screening = new Screening();
        screening.setMovie(movie);
        screening.setHall(hall);
        screening.setStartTime(OffsetDateTime.parse((String) dtoMap.get("startTime")));
        screening.setRoomNumber((Integer) dtoMap.get("roomNumber"));
        screening.setCapacity((Integer) dtoMap.get("capacity"));

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }

    @PutMapping("/{id}")
    public ScreeningDTO update(@PathVariable UUID id, @RequestBody Map<String, Object> dtoMap) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        UUID movieId = UUID.fromString(((Map<String, String>) dtoMap.get("movie")).get("id"));
        UUID hallId = UUID.fromString(((Map<String, String>) dtoMap.get("hall")).get("id"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        screening.setMovie(movie);
        screening.setHall(hall);
        screening.setStartTime(OffsetDateTime.parse((String) dtoMap.get("startTime")));
        screening.setRoomNumber((Integer) dtoMap.get("roomNumber"));
        screening.setCapacity((Integer) dtoMap.get("capacity"));

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }
    @PatchMapping("/{id}")
    public ScreeningDTO patch(@PathVariable UUID id, @RequestBody Map<String, Object> dtoMap) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        // Start time
        if (dtoMap.containsKey("startTime") && dtoMap.get("startTime") != null) {
            screening.setStartTime(OffsetDateTime.parse(dtoMap.get("startTime").toString()));
        }

        // Room number
        if (dtoMap.containsKey("roomNumber") && dtoMap.get("roomNumber") != null) {
            screening.setRoomNumber((Integer) dtoMap.get("roomNumber"));
        }

        // Capacity
        if (dtoMap.containsKey("capacity") && dtoMap.get("capacity") != null) {
            screening.setCapacity((Integer) dtoMap.get("capacity"));
        }

        // Movie
        if (dtoMap.containsKey("movie") && dtoMap.get("movie") != null) {
            Map<String, Object> movieMap = (Map<String, Object>) dtoMap.get("movie");
            if (movieMap.get("id") != null) {
                UUID movieId = UUID.fromString(movieMap.get("id").toString());
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new RuntimeException("Movie not found"));
                screening.setMovie(movie);
            }
        }

        // Hall
        if (dtoMap.containsKey("hall") && dtoMap.get("hall") != null) {
            Map<String, Object> hallMap = (Map<String, Object>) dtoMap.get("hall");
            if (hallMap.get("id") != null) {
                UUID hallId = UUID.fromString(hallMap.get("id").toString());
                Hall hall = hallRepository.findById(hallId)
                        .orElseThrow(() -> new RuntimeException("Hall not found"));
                screening.setHall(hall);
            }
        }

        screening = screeningRepository.save(screening);
        return toDTO(screening);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        screeningRepository.deleteById(id);
    }
}
