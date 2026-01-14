package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Member user entity representing registered customers
 * Extends User with membership-specific features
 */
@Entity
@DiscriminatorValue("MEMBER")
public class Member extends User {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "membership_tier")
    private String membershipTier;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Default constructor for JPA
     */
    protected Member() {
        super();
    }

    /**
     * Create member user
     * @param id - user identifier
     * @param email - email address
     * @param username - username
     * @param passwordHash - hashed password
     * @param firstName - first name
     * @param lastName - last name
     */
    public Member(UserId id, Email email, String username, String passwordHash,
                  String firstName, String lastName) {
        super(id, email, username, passwordHash);
        this.firstName = firstName;
        this.lastName = lastName;
        this.membershipTier = "BRONZE";
        this.loyaltyPoints = 0;
    }

    /**
     * Get first name
     * @return String - first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get last name
     * @return String - last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get full name
     * @return String - first and last name combined
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Get phone number
     * @return String - phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Get membership tier
     * @return String - tier (BRONZE, SILVER, GOLD, PLATINUM)
     */
    public String getMembershipTier() {
        return membershipTier;
    }

    /**
     * Get loyalty points
     * @return Integer - accumulated points
     */
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * Get date of birth
     * @return LocalDate - birth date
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Update personal information
     * @param firstName - first name
     * @param lastName - last name
     * @param phoneNumber - phone number
     */
    public void updatePersonalInfo(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Set date of birth
     * @param dateOfBirth - birth date
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Add loyalty points and update tier if needed
     * @param points - points to add
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateMembershipTier();
    }

    /**
     * Deduct loyalty points
     * @param points - points to deduct
     * @return boolean - true if successful
     */
    public boolean deductLoyaltyPoints(int points) {
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
            updateMembershipTier();
            return true;
        }
        return false;
    }

    /**
     * Update membership tier based on loyalty points
     */
    private void updateMembershipTier() {
        if (loyaltyPoints >= 10000) {
            this.membershipTier = "PLATINUM";
        } else if (loyaltyPoints >= 5000) {
            this.membershipTier = "GOLD";
        } else if (loyaltyPoints >= 1000) {
            this.membershipTier = "SILVER";
        } else {
            this.membershipTier = "BRONZE";
        }
    }

    /**
     * Get discount percentage based on membership tier
     * @return double - discount percentage
     */
    public double getTierDiscount() {
        return switch (membershipTier) {
            case "PLATINUM" -> 0.15;
            case "GOLD" -> 0.10;
            case "SILVER" -> 0.05;
            default -> 0.0;
        };
    }

    @Override
    public String getUserType() {
        return "MEMBER";
    }

    @Override
    public String getDisplayName() {
        return String.format("%s (%s)", getFullName(), membershipTier);
    }

    @Override
    public boolean hasAdminPrivileges() {
        return false;
    }
}
