package com.stampify.passport.dto;

import com.stampify.passport.enums.UserRole;

public class CreateUserRequest {

    private UserRole role;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Shared
    private Long organizationId;

    // Member-only
    private String membershipNumber;
    private String membershipStatus;

    // Scanner-only
    private String deviceIdentifier;

    // getters & setters
}
