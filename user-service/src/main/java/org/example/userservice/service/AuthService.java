package org.example.userservice.service;


import org.example.userservice.dto.LoginRequest;
import org.example.userservice.dto.LoginResponse;
import org.example.userservice.dto.PasswordResetConfirm;
import org.example.userservice.dto.PasswordResetRequest;
import org.example.userservice.dto.RegisterRequest;
import org.example.userservice.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    boolean activate(String token);

    LoginResponse login(LoginRequest request);

    void requestPasswordReset(PasswordResetRequest request);

    boolean confirmPasswordReset(PasswordResetConfirm request);
}
