package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;

    public List<HallDTO> getAllHalls() {
        return hallRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public HallDTO getHallById(UUID id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));
        return toDTO(hall);
    }

    public HallDTO createHall(HallDTO dto) {
        Hall hall = toEntity(dto);
        hall.setId(null);
        hall = hallRepository.save(hall);
        return toDTO(hall);
    }

    public HallDTO updateHall(UUID id, HallDTO dto) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));

        hall.setNumber(dto.getNumber());
        hall.setCapacity(dto.getCapacity());
        hall.setName(dto.getName());

        hall = hallRepository.save(hall);
        return toDTO(hall);
    }

    public HallDTO patchHall(UUID id, HallDTO dto) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));

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

    public void deleteHall(UUID id) {
        if (!hallRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found");
        }
        hallRepository.deleteById(id);
    }

    // Metode helper pentru conversie
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
}