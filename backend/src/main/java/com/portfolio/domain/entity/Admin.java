package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;

import jakarta.persistence.*;

/**
 * Admin user entity with full system privileges
 * Extends User with administrative capabilities
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(name = "department")
    private String department;

    @Column(name = "admin_level")
    private Integer adminLevel;

    /**
     * Default constructor for JPA
     */
    protected Admin() {
        super();
    }

    /**
     * Create admin user
     * @param id - user identifier
     * @param email - email address
     * @param username - username
     * @param passwordHash - hashed password
     * @param department - admin department
     * @param adminLevel - admin privilege level (1-5)
     */
    public Admin(UserId id, Email email, String username, String passwordHash,
                 String department, Integer adminLevel) {
        super(id, email, username, passwordHash);
        this.department = department;
        this.adminLevel = validateAdminLevel(adminLevel);
    }

    /**
     * Validate admin level is within range
     * @param level - admin level to validate
     * @return Integer - validated level
     */
    private Integer validateAdminLevel(Integer level) {
        if (level == null || level < 1 || level > 5) {
            throw new IllegalArgumentException("Admin level must be between 1 and 5");
        }
        return level;
    }

    /**
     * Get department
     * @return String - department name
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Get admin level
     * @return Integer - admin privilege level
     */
    public Integer getAdminLevel() {
        return adminLevel;
    }

    /**
     * Update admin department
     * @param department - new department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Update admin level
     * @param adminLevel - new admin level
     */
    public void setAdminLevel(Integer adminLevel) {
        this.adminLevel = validateAdminLevel(adminLevel);
    }

    /**
     * Check if admin is super admin (level 5)
     * @return boolean - true if super admin
     */
    public boolean isSuperAdmin() {
        return adminLevel != null && adminLevel == 5;
    }

    @Override
    public String getUserType() {
        return "ADMIN";
    }

    @Override
    public String getDisplayName() {
        return String.format("[Admin] %s (%s)", getUsername(), department);
    }

    @Override
    public boolean hasAdminPrivileges() {
        return true;
    }
}
