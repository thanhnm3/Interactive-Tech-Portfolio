package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Guest entity
 */
@DisplayName("Guest Entity Tests")
class GuestTest {

    @Test
    @DisplayName("Should create guest user with session tracking")
    void shouldCreateGuestUserWithSessionTracking() {
        Guest guest = new Guest(
                UserId.generate(),
                "session-123",
                "192.168.1.1",
                "Mozilla/5.0");

        assertThat(guest.getUserType()).isEqualTo("GUEST");
        assertThat(guest.hasAdminPrivileges()).isFalse();
        assertThat(guest.getSessionId()).isEqualTo("session-123");
        assertThat(guest.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(guest.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(guest.isConverted()).isFalse();
        assertThat(guest.isSessionValid()).isTrue();
    }

    @Test
    @DisplayName("Should check if session is valid")
    void shouldCheckIfSessionIsValid() {
        Guest guest = new Guest(
                UserId.generate(),
                "session-123",
                "192.168.1.1",
                "Mozilla/5.0");

        assertThat(guest.isSessionValid()).isTrue();
        assertThat(guest.getSessionExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should extend session")
    void shouldExtendSession() {
        Guest guest = new Guest(
                UserId.generate(),
                "session-123",
                "192.168.1.1",
                "Mozilla/5.0");

        LocalDateTime originalExpiry = guest.getSessionExpiresAt();
        guest.extendSession(48);

        assertThat(guest.getSessionExpiresAt()).isAfter(originalExpiry);
    }

    @Test
    @DisplayName("Should mark guest as converted")
    void shouldMarkGuestAsConverted() {
        Guest guest = new Guest(
                UserId.generate(),
                "session-123",
                "192.168.1.1",
                "Mozilla/5.0");

        guest.markAsConverted();

        assertThat(guest.isConverted()).isTrue();
        assertThat(guest.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should update tracking information")
    void shouldUpdateTrackingInfo() {
        Guest guest = new Guest(
                UserId.generate(),
                "session-123",
                "192.168.1.1",
                "Mozilla/5.0");

        guest.updateTrackingInfo("192.168.1.2", "Chrome/120.0");

        assertThat(guest.getIpAddress()).isEqualTo("192.168.1.2");
        assertThat(guest.getUserAgent()).isEqualTo("Chrome/120.0");
    }

    @Test
    @DisplayName("Should get display name")
    void shouldGetDisplayName() {
        Guest guest = new Guest(
                UserId.generate(),
                "session-12345678",
                "192.168.1.1",
                "Mozilla/5.0");

        assertThat(guest.getDisplayName()).contains("Guest User");
        assertThat(guest.getDisplayName()).contains("session-12");
    }
}
