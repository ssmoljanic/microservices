package org.example.userservice.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;


    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean activated = false;

    @Column(nullable = false)
    private boolean blocked = false;

    @Column(nullable = false)
    private int sessionsReportedTotal = 0;

    @Column(nullable = false)
    private int sessionsAttended = 0;

    @Column(nullable = false)
    private int sessionsMissed = 0;

    @Column(nullable = false)
    private double attendancePercent = 100.0;

    @Column(nullable = false)
    private int sessionsOrganizedSuccessful = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizerTitle organizerTitle = OrganizerTitle.NONE;


    public User(){}

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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

    public double getAttendancePercent() {
        return attendancePercent;
    }

    public void setAttendancePercent(double attendancePercent) {
        this.attendancePercent = attendancePercent;
    }

    public int getSessionsOrganizedSuccessful() {
        return sessionsOrganizedSuccessful;
    }

    public void setSessionsOrganizedSuccessful(int sessionsOrganizedSuccessful) {
        this.sessionsOrganizedSuccessful = sessionsOrganizedSuccessful;
    }

    public OrganizerTitle getOrganizerTitle() {
        return organizerTitle;
    }

    public void setOrganizerTitle(OrganizerTitle organizerTitle) {
        this.organizerTitle = organizerTitle;
    }
}
