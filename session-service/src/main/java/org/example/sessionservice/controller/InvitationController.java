package org.example.sessionservice.controller;

import io.jsonwebtoken.Claims;
import org.example.sessionservice.security.CheckSecurity;
import org.example.sessionservice.security.service.TokenService;
import org.example.sessionservice.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final SessionService sessionService;
    private final TokenService tokenService;

    public InvitationController(SessionService sessionService, TokenService tokenService) {
        this.sessionService = sessionService;
        this.tokenService = tokenService;
    }

    @PostMapping("/accept")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<?> accept(@RequestParam String token,
                                    @RequestHeader("Authorization") String authorization) {

        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        sessionService.acceptInvitation(token, userId, authorization);
        return ResponseEntity.ok().build();
    }
}
