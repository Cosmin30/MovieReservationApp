package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream().map(movie -> {
            MovieDTO dto = new MovieDTO();
            dto.setId(movie.getId());
            dto.setTitle(movie.getTitle());
            dto.setDescription(movie.getDescription());
            dto.setGenre(movie.getGenre());
            dto.setDuration(movie.getDuration());
            dto.setReleaseDate(movie.getReleaseDate());
            return dto;
        }).collect(Collectors.toList());
    }

    public MovieDTO getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setGenre(movie.getGenre());
        dto.setDuration(movie.getDuration());
        dto.setReleaseDate(movie.getReleaseDate());
        return dto;
    }
}
