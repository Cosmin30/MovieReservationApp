package com.example.MovieReservationApp.infrastructure.persistence.repository;

import com.example.MovieReservationApp.domain.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setup() {

        user1 = new User();
        user1.setFullName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPasswordHash("pwd1");
        user1.setCreatedAt(OffsetDateTime.now());

        user2 = new User();
        user2.setFullName("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setPasswordHash("pwd2");
        user2.setCreatedAt(OffsetDateTime.now());

        user3 = new User();
        user3.setFullName("Johnny Bravo");
        user3.setEmail("johnny.bravo@example.com");
        user3.setPasswordHash("pwd3");
        user3.setCreatedAt(OffsetDateTime.now());

        userRepository.saveAll(List.of(user1, user2, user3));
    }

    @Test
    @DisplayName("Find user by email")
    void testFindByEmail() {
        var result = userRepository.findByEmail("john.doe@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Return empty when email not found")
    void testFindByEmailNotFound() {
        var result = userRepository.findByEmail("absent@example.com");

        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Find users by full name containing ignore case")
    void testFindByFullNameContainingIgnoreCase() {
        var result = userRepository.findByFullNameContainingIgnoreCase("doe");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.doe@example.com");
    }

    @Test
    @DisplayName("Exists by email returns true")
    void testExistsByEmailTrue() {
        boolean exists = userRepository.existsByEmail("jane.doe@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Exists by email returns false")
    void testExistsByEmailFalse() {
        boolean exists = userRepository.existsByEmail("nobody@example.com");

        assertThat(exists).isFalse();
    }
}
