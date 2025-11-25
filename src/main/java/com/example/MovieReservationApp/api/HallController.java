package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.application.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @GetMapping
    public List<HallDTO> getAll() {
        return hallService.getAllHalls();
    }

    @GetMapping("/{id}")
    public HallDTO getById(@PathVariable UUID id) {
        return hallService.getHallById(id);
    }

    @PostMapping
    public HallDTO create(@RequestBody HallDTO dto) {
        return hallService.createHall(dto);
    }

    @PutMapping("/{id}")
    public HallDTO update(@PathVariable UUID id, @RequestBody HallDTO dto) {
        return hallService.updateHall(id, dto);
    }

    @PatchMapping("/{id}")
    public HallDTO patch(@PathVariable UUID id, @RequestBody HallDTO dto) {
        return hallService.patchHall(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        hallService.deleteHall(id);
    }
}