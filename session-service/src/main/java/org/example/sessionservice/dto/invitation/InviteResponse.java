package org.example.sessionservice.dto.invitation;

public class InviteResponse {

    private String token;

    public InviteResponse() {}

    public InviteResponse(String token) { this.token = token; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}

