package com.example.MovieReservationApp.infrastructure.persistence.repository;

import com.example.MovieReservationApp.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByFullNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);
}

