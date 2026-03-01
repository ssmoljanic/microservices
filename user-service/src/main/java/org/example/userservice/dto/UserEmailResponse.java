package org.example.userservice.dto;

public class UserEmailResponse {
    private Long userId;
    private String email;

    public UserEmailResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public UserEmailResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
