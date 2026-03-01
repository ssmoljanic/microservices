package org.example.sessionservice.service;

import org.example.sessionservice.dto.game.CreateGameRequest;
import org.example.sessionservice.dto.game.GameResponse;
import org.example.sessionservice.dto.game.UpdateGameRequest;

import java.util.List;

public interface GameService {

    GameResponse create(CreateGameRequest request);
    GameResponse update(Long id, UpdateGameRequest request);
    GameResponse getById(Long id);
    List<GameResponse> getAll();

}

