package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallRepository hallRepository;

    private HallDTO toDTO(Hall hall) {
        HallDTO dto = new HallDTO();
        dto.setId(hall.getId());
        dto.setNumber(hall.getNumber());
        dto.setCapacity(hall.getCapacity());
        dto.setName(hall.getName());
        return dto;
    }

    private Hall toEntity(HallDTO dto) {
        Hall hall = new Hall();
        hall.setId(dto.getId());
        hall.setNumber(dto.getNumber());
        hall.setCapacity(dto.getCapacity());
        hall.setName(dto.getName());
        return hall;
    }

    @GetMapping
    public List<HallDTO> getAll() {
        return hallRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public HallDTO getById(@PathVariable UUID id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));
        return toDTO(hall);
    }

    @PostMapping
    public HallDTO create(@RequestBody HallDTO dto) {
        Hall hall = toEntity(dto);
        hall.setId(null);
        hall = hallRepository.save(hall);
        return toDTO(hall);
    }

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

    @PatchMapping("/{id}")
    public HallDTO patch(@PathVariable UUID id, @RequestBody HallDTO dto) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        if (dto.getName() != null) {
            hall.setName(dto.getName());
        }
        if (dto.getCapacity() != null) {
            hall.setCapacity(dto.getCapacity());
        }
        if (dto.getNumber() != null) {
            hall.setNumber(dto.getNumber());
        }

        hall = hallRepository.save(hall);
        return toDTO(hall);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        if (!hallRepository.existsById(id)) {
            throw new RuntimeException("Hall not found");
        }
        hallRepository.deleteById(id);
    }
}
