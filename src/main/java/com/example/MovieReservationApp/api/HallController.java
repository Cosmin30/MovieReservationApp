package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    @Autowired
    private HallRepository hallRepository;

    // ENTITY → DTO
    private HallDTO toDTO(Hall hall) {
        return new HallDTO(
                hall.getId(),
                hall.getNumber(),
                hall.getCapacity(),
                hall.getName()
        );
    }

    // DTO → ENTITY
    private Hall toEntity(HallDTO dto) {
        Hall hall = new Hall();
        hall.setId(dto.getId());
        hall.setNumber(dto.getNumber());
        hall.setCapacity(dto.getCapacity());
        hall.setName(dto.getName());
        return hall;
    }

    // GET ALL
    @GetMapping
    public List<HallDTO> getAll() {
        return hallRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public HallDTO getById(@PathVariable UUID id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));
        return toDTO(hall);
    }

    // CREATE
    @PostMapping
    public HallDTO create(@RequestBody HallDTO dto) {
        Hall hall = toEntity(dto);
        hall.setId(null);
        hall = hallRepository.save(hall);
        return toDTO(hall);
    }

    // UPDATE
    @PutMapping("/{id}")
    public HallDTO update(@PathVariable UUID id, @RequestBody HallDTO dto) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        hall.setNumber(dto.getNumber());
        hall.setCapacity(dto.getCapacity());
        hall.setName(dto.getName());

        hall = hallRepository.save(hall);
        return toDTO(hall);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        hallRepository.deleteById(id);
    }
}
