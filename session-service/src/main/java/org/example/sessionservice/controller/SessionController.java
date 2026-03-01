package org.example.sessionservice.controller;

import io.jsonwebtoken.Claims;
import org.example.sessionservice.domain.SessionType;
import org.example.sessionservice.dto.CreateSessionRequest;
import org.example.sessionservice.dto.FinishRequest;
import org.example.sessionservice.dto.SessionResponse;
import org.example.sessionservice.dto.invitation.InviteRequest;
import org.example.sessionservice.dto.invitation.InviteResponse;
import org.example.sessionservice.security.CheckSecurity;
import org.example.sessionservice.security.service.TokenService;
import org.example.sessionservice.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final TokenService tokenService;

    public SessionController(SessionService sessionService, TokenService tokenService){
        this.sessionService = sessionService;
        this.tokenService = tokenService;
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<SessionResponse> create(@RequestHeader("Authorization") String authorization,
                                                  @RequestBody CreateSessionRequest request){
        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long organizerId = claims.get("userId", Long.class);

        SessionResponse created = sessionService.create(organizerId, authorization, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getById(id));
    }


    @PostMapping("/{id}/join")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<?> join(@PathVariable Long id,
                                  @RequestHeader("Authorization") String authorization) {

        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        sessionService.join(id, userId, authorization);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<?> leave(@PathVariable Long id,
                                   @RequestHeader("Authorization") String authorization) {

        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        sessionService.leave(id, userId, authorization);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finish")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<?> finish(@PathVariable Long id,
                                    @RequestHeader("Authorization") String authorization,
                                    @RequestBody FinishRequest request) {

        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        sessionService.finish(id, userId, authorization, request.getAttendedUserIds());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/cancel")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<?> cancel(@PathVariable Long id,
                                    @RequestHeader("Authorization") String authorization) {

        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);

        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);

        sessionService.cancel(id, userId, role);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/invite")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<InviteResponse> invite(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String authorization,
                                                 @RequestBody InviteRequest request) {

        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        return ResponseEntity.ok(sessionService.invite(id, userId, request.getInvitedUserId()));
    }

    @GetMapping
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<?> search(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) SessionType type,
            @RequestParam(required = false) Integer maxPlayers,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "false") boolean joined,
            @RequestParam(required = false, defaultValue = "startTime") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        return ResponseEntity.ok(sessionService.search(gameId, type, maxPlayers, keyword, joined, userId, sortBy, direction));
    }


}
