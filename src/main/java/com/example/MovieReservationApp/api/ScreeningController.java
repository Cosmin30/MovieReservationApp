package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.ScreeningDTO;
import com.example.MovieReservationApp.application.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    @GetMapping
    public List<ScreeningDTO> getAllScreenings() {
        return screeningService.getAllScreenings();
    }

    @GetMapping("/{id}")
    public ScreeningDTO getScreeningById(@PathVariable UUID id) {
        return screeningService.getScreeningById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScreeningDTO createScreening(@RequestBody ScreeningDTO dto) {
        return screeningService.createScreening(dto);
    }

    @PutMapping("/{id}")
    public ScreeningDTO updateScreening(@PathVariable UUID id, @RequestBody ScreeningDTO dto) {
        return screeningService.updateScreening(id, dto);
    }

    @PatchMapping("/{id}")
    public ScreeningDTO patchScreening(@PathVariable UUID id, @RequestBody ScreeningDTO dto) {
        return screeningService.patchScreening(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScreening(@PathVariable UUID id) {
        screeningService.deleteScreening(id);
    }
}
