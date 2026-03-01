package org.example.sessionservice.service.impl;

import org.example.sessionservice.domain.Game;
import org.example.sessionservice.dto.game.CreateGameRequest;
import org.example.sessionservice.dto.game.GameResponse;
import org.example.sessionservice.dto.game.UpdateGameRequest;
import org.example.sessionservice.repository.GameRepository;
import org.example.sessionservice.service.GameService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameResponse create(CreateGameRequest request) {
        if (request.getName() == null || request.getName().isBlank())
            throw new RuntimeException("name is required");
        if (request.getGenre() == null || request.getGenre().isBlank())
            throw new RuntimeException("genre is required");

        String name = request.getName().trim();
        if (gameRepository.existsByName(name))
            throw new RuntimeException("game name already exists");

        Game g = new Game();
        g.setName(name);
        g.setGenre(request.getGenre().trim());
        g.setDescription(request.getDescription());

        return toDto(gameRepository.save(g));
    }

    @Override
    public GameResponse update(Long id, UpdateGameRequest request) {
        Game g = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            String newName = request.getName().trim();
            if (!newName.equals(g.getName()) && gameRepository.existsByName(newName)) {
                throw new RuntimeException("game name already exists");
            }
            g.setName(newName);
        }

        if (request.getGenre() != null && !request.getGenre().isBlank()) {
            g.setGenre(request.getGenre().trim());
        }

        if (request.getDescription() != null) {
            g.setDescription(request.getDescription());
        }

        return toDto(gameRepository.save(g));
    }

    @Override
    public GameResponse getById(Long id) {
        return toDto(gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found")));
    }

    @Override
    public List<GameResponse> getAll() {
        return gameRepository.findAll().stream().map(this::toDto).toList();
    }

    private GameResponse toDto(Game g) {
        GameResponse dto = new GameResponse();
        dto.setId(g.getId());
        dto.setName(g.getName());
        dto.setDescription(g.getDescription());
        dto.setGenre(g.getGenre());
        return dto;
    }
}
