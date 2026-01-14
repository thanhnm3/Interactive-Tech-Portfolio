package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Guest user entity representing temporary/anonymous users
 * Extends User with guest-specific features and session tracking
 */
@Entity
@DiscriminatorValue("GUEST")
public class Guest extends User {

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "session_expires_at")
    private LocalDateTime sessionExpiresAt;

    @Column(name = "is_converted")
    private boolean isConverted;

    /**
     * Default constructor for JPA
     */
    protected Guest() {
        super();
    }

    /**
     * Create guest user with session tracking
     * @param id - user identifier
     * @param sessionId - browser session ID
     * @param ipAddress - client IP address
     * @param userAgent - browser user agent string
     */
    public Guest(UserId id, String sessionId, String ipAddress, String userAgent) {
        super(id, Email.of("guest_" + id.getValue().toString().substring(0, 8) + "@temp.local"),
              "guest_" + id.getValue().toString().substring(0, 8), "N/A");
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionExpiresAt = LocalDateTime.now().plusHours(24);
        this.isConverted = false;
    }

    /**
     * Get session ID
     * @return String - session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Get IP address
     * @return String - client IP
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Get user agent
     * @return String - browser user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Get session expiration time
     * @return LocalDateTime - expiration timestamp
     */
    public LocalDateTime getSessionExpiresAt() {
        return sessionExpiresAt;
    }

    /**
     * Check if guest has been converted to member
     * @return boolean - true if converted
     */
    public boolean isConverted() {
        return isConverted;
    }

    /**
     * Check if session is still valid
     * @return boolean - true if not expired
     */
    public boolean isSessionValid() {
        return LocalDateTime.now().isBefore(sessionExpiresAt);
    }

    /**
     * Extend session by specified hours
     * @param hours - hours to extend
     */
    public void extendSession(int hours) {
        this.sessionExpiresAt = LocalDateTime.now().plusHours(hours);
    }

    /**
     * Mark guest as converted to member
     */
    public void markAsConverted() {
        this.isConverted = true;
        deactivate();
    }

    /**
     * Update tracking information
     * @param ipAddress - new IP address
     * @param userAgent - new user agent
     */
    public void updateTrackingInfo(String ipAddress, String userAgent) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    @Override
    public String getUserType() {
        return "GUEST";
    }

    @Override
    public String getDisplayName() {
        return "Guest User (" + sessionId.substring(0, 8) + "...)";
    }

    @Override
    public boolean hasAdminPrivileges() {
        return false;
    }
}
