package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;

    // ENTITY → DTO
    private MovieDTO toDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setReleaseDate(movie.getReleaseDate());
        return dto;
    }

    // DTO → ENTITY
    private Movie toEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setReleaseDate(dto.getReleaseDate());
        return movie;
    }

    // --- GET ALL ---
    @GetMapping
    public List<MovieDTO> getAll() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- GET BY ID ---
    @GetMapping("/{id}")
    public MovieDTO getById(@PathVariable UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return toDTO(movie);
    }

    // --- CREATE ---
    @PostMapping
    public MovieDTO create(@RequestBody MovieDTO dto) {
        Movie movie = toEntity(dto);
        movie.setId(null);
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    // --- UPDATE (full update) ---
    @PutMapping("/{id}")
    public MovieDTO update(@PathVariable UUID id, @RequestBody MovieDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setReleaseDate(dto.getReleaseDate());

        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    // --- PATCH (partial update) ---
    @PatchMapping("/{id}")
    public MovieDTO patch(@PathVariable UUID id, @RequestBody MovieDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (dto.getTitle() != null) {
            movie.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            movie.setDescription(dto.getDescription());
        }
        if (dto.getGenre() != null) {
            movie.setGenre(dto.getGenre());
        }
        if (dto.getDuration() != null) {
            movie.setDuration(dto.getDuration());
        }
        if (dto.getReleaseDate() != null) {
            movie.setReleaseDate(dto.getReleaseDate());
        }

        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found");
        }
        movieRepository.deleteById(id);
    }
}
