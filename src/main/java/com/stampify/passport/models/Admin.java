package com.stampify.passport.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    private LocalDateTime createdAt;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
