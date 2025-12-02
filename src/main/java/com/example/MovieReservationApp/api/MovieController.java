package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.application.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public List<MovieDTO> getAll() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieDTO getById(@PathVariable UUID id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    public MovieDTO create(@RequestBody MovieDTO dto) {
        return movieService.createMovie(dto);
    }

    @PutMapping("/{id}")
    public MovieDTO update(@PathVariable UUID id, @RequestBody MovieDTO dto) {
        return movieService.updateMovie(id, dto);
    }

    @PatchMapping("/{id}")
    public MovieDTO patch(@PathVariable UUID id, @RequestBody MovieDTO dto) {
        return movieService.patchMovie(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        movieService.deleteMovie(id);
    }
}