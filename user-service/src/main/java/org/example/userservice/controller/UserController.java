package org.example.userservice.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.example.userservice.dto.UpdateProfileRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.security.CheckSecurity;
import org.example.userservice.security.service.TokenService;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/me")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<UserResponse> me(@RequestHeader("Authorization") String authorization) {
        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        return ResponseEntity.ok(userService.getMe(userId));
    }

    @PutMapping("/me")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_PLAYER"})
    public ResponseEntity<UserResponse> updateMe(@RequestHeader("Authorization") String authorization,
                                                 @Valid @RequestBody UpdateProfileRequest request) {
        String jwt = authorization.split(" ")[1];
        Claims claims = tokenService.parseToken(jwt);
        Long userId = claims.get("userId", Long.class);

        return ResponseEntity.ok(userService.updateMe(userId, request));
    }

}