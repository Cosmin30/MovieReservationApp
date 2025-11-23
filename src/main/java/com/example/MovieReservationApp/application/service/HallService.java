package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;

    public List<HallDTO> getAllHalls() {
        return hallRepository.findAll().stream().map(hall -> {
            HallDTO dto = new HallDTO();
            dto.setId(hall.getId());
            dto.setName(hall.getName());
            dto.setNumber(hall.getNumber());
            dto.setCapacity(hall.getCapacity());
            return dto;
        }).collect(Collectors.toList());
    }

    public HallDTO getHallById(UUID id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        HallDTO dto = new HallDTO();
        dto.setId(hall.getId());
        dto.setName(hall.getName());
        dto.setNumber(hall.getNumber());
        dto.setCapacity(hall.getCapacity());
        return dto;
    }
}
