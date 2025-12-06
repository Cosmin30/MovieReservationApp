package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.HallDTO;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.HallRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.example.MovieReservationApp.config.TestSecurityConfig.class)
class HallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        // Șterge în ordinea corectă pentru a respecta constrângerile de chei străine
        paymentRepository.deleteAll();
        ticketRepository.deleteAll();
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        screeningRepository.deleteAll();
        hallRepository.deleteAll();
    }

    @Test
    void testCreateMultipleHalls() throws Exception {
        // Creăm 5 săli diferite
        String[] hallNames = {"Sala Premium", "Sala VIP", "Sala IMAX", "Sala 4DX", "Sala Standard"};
        int[] capacities = {150, 80, 300, 120, 200};
        int[] numbers = {1, 2, 3, 4, 5};

        for (int i = 0; i < 5; i++) {
            HallDTO dto = new HallDTO();
            dto.setName(hallNames[i]);
            dto.setNumber(numbers[i]);
            dto.setCapacity(capacities[i]);

            mockMvc.perform(post("/api/halls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(hallNames[i]))
                    .andExpect(jsonPath("$.number").value(numbers[i]))
                    .andExpect(jsonPath("$.capacity").value(capacities[i]));
        }

        // Verificăm că toate cele 5 săli au fost salvate în BD
        assertThat(hallRepository.count()).isEqualTo(5);

        // Verificăm că le putem citi toate
        mockMvc.perform(get("/api/halls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testGetAll() throws Exception {
        // Arrange - creăm 5 săli
        Hall h1 = new Hall();
        h1.setNumber(1);
        h1.setCapacity(100);
        h1.setName("Sala A");
        hallRepository.save(h1);

        Hall h2 = new Hall();
        h2.setNumber(2);
        h2.setCapacity(200);
        h2.setName("Sala B");
        hallRepository.save(h2);

        Hall h3 = new Hall();
        h3.setNumber(3);
        h3.setCapacity(150);
        h3.setName("Sala C");
        hallRepository.save(h3);

        Hall h4 = new Hall();
        h4.setNumber(4);
        h4.setCapacity(180);
        h4.setName("Sala D");
        hallRepository.save(h4);

        Hall h5 = new Hall();
        h5.setNumber(5);
        h5.setCapacity(250);
        h5.setName("Sala E");
        hallRepository.save(h5);

        // Act & Assert
        mockMvc.perform(get("/api/halls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Sala A"))
                .andExpect(jsonPath("$[1].capacity").value(200))
                .andExpect(jsonPath("$[4].name").value("Sala E"));
    }

    @Test
    void testGetById() throws Exception {
        // Arrange
        Hall hall = new Hall();
        hall.setNumber(5);
        hall.setCapacity(300);
        hall.setName("VIP Room");
        Hall saved = hallRepository.save(hall);

        // Act & Assert
        mockMvc.perform(get("/api/halls/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("VIP Room"))
                .andExpect(jsonPath("$.capacity").value(300));
    }

    @Test
    void testCreate() throws Exception {
        // Arrange
        HallDTO dto = new HallDTO();
        dto.setName("Test Hall");
        dto.setNumber(7);
        dto.setCapacity(250);

        // Act
        String response = mockMvc.perform(post("/api/halls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(7))
                .andExpect(jsonPath("$.capacity").value(250))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert - verificăm persistența în BD
        HallDTO created = objectMapper.readValue(response, HallDTO.class);
        Hall hallInDb = hallRepository.findById(created.getId()).orElse(null);

        assertThat(hallInDb).isNotNull();
        assertThat(hallInDb.getName()).isEqualTo("Test Hall");
        assertThat(hallInDb.getNumber()).isEqualTo(7);
        assertThat(hallInDb.getCapacity()).isEqualTo(250);
    }

    @Test
    void testUpdate() throws Exception {
        // Arrange
        Hall hall = new Hall();
        hall.setName("Sala");
        hall.setNumber(8);
        hall.setCapacity(300);
        Hall saved = hallRepository.save(hall);

        HallDTO dto = new HallDTO();
        dto.setName("Sala fericirii");
        dto.setNumber(8);
        dto.setCapacity(400);

        // Act
        mockMvc.perform(put("/api/halls/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sala fericirii"))
                .andExpect(jsonPath("$.capacity").value(400));

        // Assert - verificăm că s-a actualizat în BD
        Hall updated = hallRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Sala fericirii");
        assertThat(updated.getCapacity()).isEqualTo(400);
    }

    @Test
    void testPatch() throws Exception {
        // Arrange
        Hall hall = new Hall();
        hall.setName("Original");
        hall.setNumber(3);
        hall.setCapacity(150);
        Hall saved = hallRepository.save(hall);

        HallDTO dto = new HallDTO();
        dto.setName("Patched");
        dto.setCapacity(200);

        // Act
        mockMvc.perform(patch("/api/halls/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patched"))
                .andExpect(jsonPath("$.capacity").value(200));

        // Assert - verificăm persistența
        Hall patched = hallRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getName()).isEqualTo("Patched");
        assertThat(patched.getCapacity()).isEqualTo(200);
        assertThat(patched.getNumber()).isEqualTo(3); // nu s-a modificat
    }

    @Test
    void testDeleteSuccess() throws Exception {
        // Arrange
        Hall hall = new Hall();
        hall.setName("To Delete");
        hall.setNumber(10);
        hall.setCapacity(100);
        Hall saved = hallRepository.save(hall);

        // Act
        mockMvc.perform(delete("/api/halls/{id}", saved.getId()))
                .andExpect(status().isOk());

        // Assert - verificăm că s-a șters din BD
        assertThat(hallRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteNotFound() throws Exception {
        // Arrange
        java.util.UUID nonExistentId = java.util.UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/halls/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}