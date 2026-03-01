package org.example.sessionservice.controller;

import org.example.sessionservice.dto.game.CreateGameRequest;
import org.example.sessionservice.dto.game.GameResponse;
import org.example.sessionservice.dto.game.UpdateGameRequest;
import org.example.sessionservice.security.CheckSecurity;
import org.example.sessionservice.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<GameResponse> create(@RequestHeader("Authorization") String authorization,
                                               @RequestBody CreateGameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.create(request));
    }

    @PutMapping("/{id}")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<GameResponse> update(@PathVariable Long id,@RequestHeader("Authorization") String authorization,
                                               @RequestBody UpdateGameRequest request) {
        return ResponseEntity.ok(gameService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> getAll() {
        return ResponseEntity.ok(gameService.getAll());
    }
}
