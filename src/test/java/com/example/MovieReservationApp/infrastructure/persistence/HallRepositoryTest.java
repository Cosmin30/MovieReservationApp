package com.example.MovieReservationApp.infrastructure.persistence;


import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HallRepositoryTest {

    @Autowired
    private HallRepository hallRepository;

    @Test
    void testSaveAndFindById() {
        Hall hall = new Hall();
        hall.setNumber(1);
        hall.setCapacity(120);
        hall.setName("Sala Mare");

        Hall saved = hallRepository.save(hall);

        Optional<Hall> found = hallRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNumber()).isEqualTo(1);
        assertThat(found.get().getCapacity()).isEqualTo(120);
        assertThat(found.get().getName()).isEqualTo("Sala Mare");
    }

    @Test
    void testFindAll() {
        Hall h1 = new Hall(null, 1, 100, "Sala 1");
        Hall h2 = new Hall(null, 2, 150, "Sala 2");

        hallRepository.save(h1);
        hallRepository.save(h2);

        var halls = hallRepository.findAll();

        assertThat(halls).hasSize(2);
    }

    @Test
    void testDelete() {
        Hall hall = new Hall(null, 3, 80, "Sala 3");

        Hall saved = hallRepository.save(hall);

        hallRepository.deleteById(saved.getId());

        assertThat(hallRepository.findById(saved.getId())).isNotPresent();
    }
}

