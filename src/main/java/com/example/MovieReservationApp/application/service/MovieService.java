package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        return toDTO(movie);
    }

    public MovieDTO createMovie(MovieDTO dto) {
        Movie movie = toEntity(dto);
        movie.setId(null); // Asigură că e nou
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    public MovieDTO updateMovie(UUID id, MovieDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setReleaseDate(dto.getReleaseDate());

        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    public MovieDTO patchMovie(UUID id, MovieDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

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

    public void deleteMovie(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        movieRepository.deleteById(id);
    }

    // Metode helper pentru conversie
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
}