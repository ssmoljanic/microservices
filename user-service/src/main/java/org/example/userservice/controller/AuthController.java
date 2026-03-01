package org.example.userservice.controller;

import jakarta.validation.Valid;
import org.example.userservice.dto.LoginRequest;
import org.example.userservice.dto.LoginResponse;
import org.example.userservice.dto.PasswordResetConfirm;
import org.example.userservice.dto.PasswordResetRequest;
import org.example.userservice.dto.RegisterRequest;
import org.example.userservice.dto.RegisterResponse;
import org.example.userservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam("token") String token){
        boolean ok = authService.activate(token);
        return ResponseEntity.ok(Map.of("activated",ok));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        // Uvek vracamo success poruku (ne otkrivamo da li email postoji)
        return ResponseEntity.ok(Map.of("message", "Ako nalog postoji, email za reset lozinke je poslat"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetConfirm request) {
        boolean ok = authService.confirmPasswordReset(request);
        return ResponseEntity.ok(Map.of("success", ok, "message", "Lozinka je uspesno promenjena"));
    }
}
