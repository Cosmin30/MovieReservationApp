package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    // ENTITY → DTO
    private MovieDTO toDTO(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDuration(),
                movie.getGenre(),
                movie.getReleaseDate()
        );
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

    @GetMapping
    public List<MovieDTO> getAll() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MovieDTO getById(@PathVariable UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return toDTO(movie);
    }

    @PostMapping
    public MovieDTO create(@RequestBody MovieDTO dto) {
        Movie movie = toEntity(dto);
        movie.setId(null);
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        movieRepository.deleteById(id);
    }
}
