package com.example.MovieReservationApp.api;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.example.MovieReservationApp.config.TestSecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void testCreateMultipleMovies() throws Exception {
        // Creăm 5 filme diferite
        String[] titles = {"Inception", "The Matrix", "Interstellar", "The Dark Knight", "Pulp Fiction"};
        String[] descriptions = {
                "A thief who steals corporate secrets through dream-sharing technology",
                "A computer hacker learns about the true nature of reality",
                "A team of explorers travel through a wormhole in space",
                "Batman fights the Joker in Gotham City",
                "The lives of two mob hitmen, a boxer, and others intertwine"
        };
        String[] genres = {"Sci-Fi", "Action", "Sci-Fi", "Action", "Crime"};
        int[] durations = {148, 136, 169, 152, 154};
        LocalDate[] releaseDates = {
                LocalDate.of(2010, 7, 16),
                LocalDate.of(1999, 3, 31),
                LocalDate.of(2014, 11, 7),
                LocalDate.of(2008, 7, 18),
                LocalDate.of(1994, 10, 14)
        };

        for (int i = 0; i < 5; i++) {
            MovieDTO dto = new MovieDTO();
            dto.setTitle(titles[i]);
            dto.setDescription(descriptions[i]);
            dto.setGenre(genres[i]);
            dto.setDuration(durations[i]);
            dto.setReleaseDate(releaseDates[i]);

            mockMvc.perform(post("/api/movies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk()) // Controller returns 200, not 201
                    .andExpect(jsonPath("$.title").value(titles[i]))
                    .andExpect(jsonPath("$.genre").value(genres[i]))
                    .andExpect(jsonPath("$.duration").value(durations[i]));
        }

        // Verificăm că toate cele 5 filme au fost salvate în BD
        assertThat(movieRepository.count()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void testGetAll() throws Exception {
        // Arrange - creăm 5 filme
        Movie m1 = new Movie();
        m1.setTitle("Avatar");
        m1.setDescription("A paraplegic Marine on an alien planet");
        m1.setGenre("Sci-Fi");
        m1.setDuration(162);
        m1.setReleaseDate(LocalDate.of(2009, 12, 18));
        movieRepository.save(m1);

        Movie m2 = new Movie();
        m2.setTitle("Titanic");
        m2.setDescription("A love story on the ill-fated ship");
        m2.setGenre("Romance");
        m2.setDuration(195);
        m2.setReleaseDate(LocalDate.of(1997, 12, 19));
        movieRepository.save(m2);

        Movie m3 = new Movie();
        m3.setTitle("The Godfather");
        m3.setDescription("The aging patriarch of a crime dynasty");
        m3.setGenre("Crime");
        m3.setDuration(175);
        m3.setReleaseDate(LocalDate.of(1972, 3, 24));
        movieRepository.save(m3);

        Movie m4 = new Movie();
        m4.setTitle("Forrest Gump");
        m4.setDescription("The presidencies of Kennedy and Johnson");
        m4.setGenre("Drama");
        m4.setDuration(142);
        m4.setReleaseDate(LocalDate.of(1994, 7, 6));
        movieRepository.save(m4);

        Movie m5 = new Movie();
        m5.setTitle("The Shawshank Redemption");
        m5.setDescription("Two imprisoned men bond over years");
        m5.setGenre("Drama");
        m5.setDuration(142);
        m5.setReleaseDate(LocalDate.of(1994, 9, 23));
        movieRepository.save(m5);

        // Act & Assert - verificăm că există cel puțin 5 filme
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(5)));
    }

    @Test
    void testGetById() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setTitle("Gladiator");
        movie.setDescription("A former Roman General sets out to exact vengeance");
        movie.setGenre("Action");
        movie.setDuration(155);
        movie.setReleaseDate(LocalDate.of(2000, 5, 5));
        Movie saved = movieRepository.save(movie);

        // Act & Assert
        mockMvc.perform(get("/api/movies/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Gladiator"))
                .andExpect(jsonPath("$.duration").value(155));
    }

    @Test
    void testCreate() throws Exception {
        // Arrange
        MovieDTO dto = new MovieDTO();
        dto.setTitle("Test Movie");
        dto.setDescription("A test movie description");
        dto.setGenre("Comedy");
        dto.setDuration(120);
        dto.setReleaseDate(LocalDate.of(2024, 1, 1));

        // Act
        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.genre").value("Comedy"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert - verificăm persistența în BD
        MovieDTO created = objectMapper.readValue(response, MovieDTO.class);
        Movie movieInDb = movieRepository.findById(created.getId()).orElse(null);

        assertThat(movieInDb).isNotNull();
        assertThat(movieInDb.getTitle()).isEqualTo("Test Movie");
        assertThat(movieInDb.getGenre()).isEqualTo("Comedy");
        assertThat(movieInDb.getDuration()).isEqualTo(120);
    }

    @Test
    void testUpdate() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setTitle("Original Title");
        movie.setDescription("Original description");
        movie.setGenre("Horror");
        movie.setDuration(100);
        movie.setReleaseDate(LocalDate.of(2020, 1, 1));
        Movie saved = movieRepository.save(movie);

        MovieDTO dto = new MovieDTO();
        dto.setTitle("Updated Title");
        dto.setDescription("Updated description");
        dto.setGenre("Thriller");
        dto.setDuration(130);
        dto.setReleaseDate(LocalDate.of(2024, 6, 15));

        // Act
        mockMvc.perform(put("/api/movies/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.genre").value("Thriller"));

        // Assert - verificăm că s-a actualizat în BD
        Movie updated = movieRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getGenre()).isEqualTo("Thriller");
        assertThat(updated.getDuration()).isEqualTo(130);
    }

    @Test
    void testPatch() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setTitle("Original");
        movie.setDescription("Original description");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2020, 5, 10));
        Movie saved = movieRepository.save(movie);

        MovieDTO dto = new MovieDTO();
        dto.setTitle("Patched Title");
        dto.setDuration(140);

        // Act
        mockMvc.perform(patch("/api/movies/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched Title"))
                .andExpect(jsonPath("$.duration").value(140));

        // Assert - verificăm persistența
        Movie patched = movieRepository.findById(saved.getId()).orElseThrow();
        assertThat(patched.getTitle()).isEqualTo("Patched Title");
        assertThat(patched.getDuration()).isEqualTo(140);
        assertThat(patched.getGenre()).isEqualTo("Action"); // nu s-a modificat
    }

    @Test
    void testDeleteSuccess() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setTitle("To Delete");
        movie.setDescription("This will be deleted");
        movie.setGenre("Horror");
        movie.setDuration(90);
        movie.setReleaseDate(LocalDate.of(2023, 10, 31));
        Movie saved = movieRepository.save(movie);

        // Act
        mockMvc.perform(delete("/api/movies/{id}", saved.getId()))
                .andExpect(status().isOk());

        // Assert - verificăm că s-a șters din BD
        assertThat(movieRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteNotFound() throws Exception {
        // Arrange
        java.util.UUID nonExistentId = java.util.UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/movies/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}