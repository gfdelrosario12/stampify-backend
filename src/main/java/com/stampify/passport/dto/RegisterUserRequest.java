package com.stampify.passport.dto;

/**
 * DTO for registering a new user.
 *
 * Organization is automatically assigned based on the email domain.
 * Role-specific fields:
 * - MEMBER: membershipNumber
 * - SCANNER: deviceIdentifier
 */
public class RegisterUserRequest {

    /* ================= GENERAL ================= */
    private String role; // ADMIN | MEMBER | SCANNER
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /* ================= MEMBER-ONLY ================= */
    private String membershipNumber;

    /* ================= SCANNER-ONLY ================= */
    private String deviceIdentifier;

    /* ================= GETTERS & SETTERS ================= */
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email != null) this.email = email.toLowerCase().trim();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMembershipNumber() { return membershipNumber; }
    public void setMembershipNumber(String membershipNumber) { this.membershipNumber = membershipNumber; }

    public String getDeviceIdentifier() { return deviceIdentifier; }
    public void setDeviceIdentifier(String deviceIdentifier) { this.deviceIdentifier = deviceIdentifier; }
}
