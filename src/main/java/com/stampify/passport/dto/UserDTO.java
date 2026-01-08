package com.stampify.passport.dto;

import java.time.LocalDateTime;

public abstract class UserDTO {

    /* ================= ID ================= */
    private Long id;

    /* ================= BASIC INFO ================= */
    private String firstName;
    private String lastName;
    private String email;

    /* ================= SECURITY ================= */
    private LocalDateTime passwordChangedAt;
    private Boolean isActive;

    /* ================= AUDIT ================= */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* ================= ROLE ================= */
    private String role;

    /* ================= ORGANIZATION ================= */
    private Long organizationId;

    /* ================= GETTERS & SETTERS ================= */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getPasswordChangedAt() { return passwordChangedAt; }
    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) { this.passwordChangedAt = passwordChangedAt; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}
