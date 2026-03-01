package org.example.sessionservice.dto.invitation;

public class InviteRequest {
    private Long invitedUserId;

    public InviteRequest() {}

    public Long getInvitedUserId() { return invitedUserId; }
    public void setInvitedUserId(Long invitedUserId) { this.invitedUserId = invitedUserId; }
}
