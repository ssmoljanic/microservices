package org.example.userservice.dto;

public class LoginResponse {

    private boolean failed;
    private String token;

    public LoginResponse(){}

    public LoginResponse(boolean failed, String token) {
        this.failed = failed;
        this.token = token;
    }

    public boolean isFailed() {
        return failed;
    }

    public String getToken() {
        return token;
    }
}
