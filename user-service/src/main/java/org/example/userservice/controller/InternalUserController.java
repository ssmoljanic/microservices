
package org.example.userservice.controller;

import org.example.userservice.dto.UserEmailResponse;
import org.example.userservice.dto.session.UpdateStatsRequest;
import org.example.userservice.dto.session.UserStatusResponse;
import org.example.userservice.security.CheckSecurity;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @CheckSecurity(roles = {"ROLE_ADMIN","ROLE_PLAYER"})
    public ResponseEntity<UserStatusResponse> getStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization) {

        return ResponseEntity.ok(userService.getUserStatus(id));
    }

    @PostMapping ("/{id}/stats")
    @CheckSecurity(roles = {"ROLE_ADMIN","ROLE_PLAYER"})
    public ResponseEntity<Void> updateStats(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdateStatsRequest request) {

        userService.updateStats(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/email")
    @CheckSecurity(roles = {"ROLE_ADMIN","ROLE_SERVICE"})
    public ResponseEntity<UserEmailResponse> getEmail(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization) {

        return ResponseEntity.ok(new UserEmailResponse(id, userService.getUserEmail(id)));
    }
}