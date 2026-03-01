package org.example.sessionservice.repository;

import org.example.sessionservice.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByName(String name);
    boolean existsByName(String name);
}
