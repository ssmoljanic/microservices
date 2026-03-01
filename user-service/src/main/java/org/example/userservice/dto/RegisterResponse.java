package org.example.userservice.dto;

public class RegisterResponse {
    private String activationToken;

    public RegisterResponse(String activationToken) {
        this.activationToken = activationToken;
    }

    public String getActivationToken() { return activationToken; }
}
