package org.example.sessionservice.dto;

public class FinishRequest {
    private java.util.List<Long> attendedUserIds;

    public FinishRequest() {}

    public java.util.List<Long> getAttendedUserIds() { return attendedUserIds; }

    public void setAttendedUserIds(java.util.List<Long> attendedUserIds) { this.attendedUserIds = attendedUserIds; }
}
