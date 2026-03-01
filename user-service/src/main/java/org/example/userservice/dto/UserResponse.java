package org.example.userservice.dto;

import org.example.userservice.domain.OrganizerTitle;

import java.time.LocalDate;

public class UserResponse {

    public Long id;
    public String username;
    public String fullName;
    public String email;
    public LocalDate dateOfBirth;
    public String role;
    public boolean activated;
    public boolean blocked;

    private int sessionsReportedTotal;
    private int sessionsAttended;
    private int sessionsMissed;
    private double attendancePercent;
    private int sessionsOrganizedSuccessful;
    private OrganizerTitle organizerTitle;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public double getAttendancePercent() {
        return attendancePercent;
    }

    public void setAttendancePercent(double attendancePercent) {
        this.attendancePercent = attendancePercent;
    }

    public OrganizerTitle getOrganizerTitle() {
        return organizerTitle;
    }

    public void setOrganizerTitle(OrganizerTitle organizerTitle) {
        this.organizerTitle = organizerTitle;
    }

    public int getSessionsReportedTotal() {
        return sessionsReportedTotal;
    }

    public void setSessionsReportedTotal(int sessionsReportedTotal) {
        this.sessionsReportedTotal = sessionsReportedTotal;
    }

    public int getSessionsAttended() {
        return sessionsAttended;
    }

    public void setSessionsAttended(int sessionsAttended) {
        this.sessionsAttended = sessionsAttended;
    }

    public int getSessionsMissed() {
        return sessionsMissed;
    }

    public void setSessionsMissed(int sessionsMissed) {
        this.sessionsMissed = sessionsMissed;
    }

    public int getSessionsOrganizedSuccessful() {
        return sessionsOrganizedSuccessful;
    }

    public void setSessionsOrganizedSuccessful(int sessionsOrganizedSuccessful) {
        this.sessionsOrganizedSuccessful = sessionsOrganizedSuccessful;
    }
}
