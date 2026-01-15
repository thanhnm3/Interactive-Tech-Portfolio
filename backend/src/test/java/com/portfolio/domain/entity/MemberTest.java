package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Member entity
 */
@DisplayName("Member Entity Tests")
class MemberTest {

    @Test
    @DisplayName("Should create member user")
    void shouldCreateMemberUser() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        assertThat(member.getUserType()).isEqualTo("MEMBER");
        assertThat(member.hasAdminPrivileges()).isFalse();
        assertThat(member.getFirstName()).isEqualTo("Taro");
        assertThat(member.getLastName()).isEqualTo("Yamada");
        assertThat(member.getFullName()).isEqualTo("Taro Yamada");
        assertThat(member.getMembershipTier()).isEqualTo("BRONZE");
        assertThat(member.getLoyaltyPoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should get display name")
    void shouldGetDisplayName() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        assertThat(member.getDisplayName()).contains("Taro Yamada");
        assertThat(member.getDisplayName()).contains("BRONZE");
    }

    @Test
    @DisplayName("Should add loyalty points and update tier")
    void shouldAddLoyaltyPointsAndUpdateTier() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        member.addLoyaltyPoints(1500);
        assertThat(member.getLoyaltyPoints()).isEqualTo(1500);
        assertThat(member.getMembershipTier()).isEqualTo("SILVER");

        member.addLoyaltyPoints(4000);
        assertThat(member.getLoyaltyPoints()).isEqualTo(5500);
        assertThat(member.getMembershipTier()).isEqualTo("GOLD");

        member.addLoyaltyPoints(5000);
        assertThat(member.getLoyaltyPoints()).isEqualTo(10500);
        assertThat(member.getMembershipTier()).isEqualTo("PLATINUM");
    }

    @Test
    @DisplayName("Should deduct loyalty points")
    void shouldDeductLoyaltyPoints() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        member.addLoyaltyPoints(1000);
        boolean success = member.deductLoyaltyPoints(500);

        assertThat(success).isTrue();
        assertThat(member.getLoyaltyPoints()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should return false when deducting more than available points")
    void shouldReturnFalseWhenDeductingMoreThanAvailablePoints() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        member.addLoyaltyPoints(100);
        boolean success = member.deductLoyaltyPoints(200);

        assertThat(success).isFalse();
        assertThat(member.getLoyaltyPoints()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should get tier discount percentage")
    void shouldGetTierDiscountPercentage() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        assertThat(member.getTierDiscount()).isEqualTo(0.0);

        member.addLoyaltyPoints(1500);
        assertThat(member.getTierDiscount()).isEqualTo(0.05);

        member.addLoyaltyPoints(4000);
        assertThat(member.getTierDiscount()).isEqualTo(0.10);

        member.addLoyaltyPoints(5000);
        assertThat(member.getTierDiscount()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("Should update personal information")
    void shouldUpdatePersonalInfo() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        member.updatePersonalInfo("Hanako", "Sato", "090-1234-5678");

        assertThat(member.getFirstName()).isEqualTo("Hanako");
        assertThat(member.getLastName()).isEqualTo("Sato");
        assertThat(member.getPhoneNumber()).isEqualTo("090-1234-5678");
    }

    @Test
    @DisplayName("Should set date of birth")
    void shouldSetDateOfBirth() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@example.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        LocalDate dob = LocalDate.of(1990, 1, 1);
        member.setDateOfBirth(dob);

        assertThat(member.getDateOfBirth()).isEqualTo(dob);
    }
}
